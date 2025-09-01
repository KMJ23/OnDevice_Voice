package com.example.rag

import android.content.Context
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import org.bsc.langgraph4j.StateGraph
import kotlinx.coroutines.*
import org.bsc.langgraph4j.state.AgentState
import javax.inject.Inject
import android.util.Log
import com.example.rag.embedding.CustomEmbeddedModel
import com.example.rag.llm.CustomChatModel
import com.example.rag.store.CustomVectorStore
import org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async
import org.bsc.langgraph4j.action.AsyncNodeActionWithConfig.node_async
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.Query
import org.bsc.langgraph4j.CompiledGraph
import org.bsc.langgraph4j.RunnableConfig
import org.bsc.langgraph4j.state.Channel
import java.util.function.Supplier

class RagState(initData: Map<String, Any>) : AgentState(initData) {
    companion object {
        val SCHEMA: Map<String, Channel<*>> = mapOf(
            "question_type" to Channel.of(Supplier { "" }),
            "filePaths" to Channel.of(Supplier { ArrayList<String>() }),
            "query" to Channel.of(Supplier { "" }),
            "search_result" to Channel.of(Supplier { "" }),
            "response" to Channel.of(Supplier { "" })
        )
    }

    fun questionType(): String {
        return value<String>("question_type").orElse("direct") // 기본값: direct
    }

    fun filePaths(): List<String> {
        return value<List<String>>("filePaths").orElse(emptyList())
    }

    fun query(): String {
        return value<String>("query").orElse("")
    }

    fun searchResult(): String {
        return value<String>("search_result").orElse("")
    }

    fun response(): String {
        return value<String>("response").orElse("")
    }
}


@ActivityRetainedScoped
class RagAgent @Inject constructor(
    private val ragManager: RagManager,
    private val customEmbeddedModel: CustomEmbeddedModel,
    private val customVectorStore: CustomVectorStore,
    private val customChatModel: CustomChatModel,
    private val scope: CoroutineScope,
    private val context: Context
) {
    //private val stateSerializer = ObjectStreamStateSerializer { RagState(HashMap()) }

    val llmNode = node_async<RagState> { state, _ ->
        val query = state.query()
        Log.d(TAG, "💡 LLM 노드 실행 - 질문 수신: $query")

        if (query.isEmpty()) {
            throw IllegalStateException("❌ 질문이 없습니다.")
        }

        // 🟢 LLM을 사용하여 질문 유형 판단
        val classification = runBlocking {
            customChatModel.classifyQuery(query) // LLM이 직접 질문 유형 판단
        }

        Log.d(TAG, "📌 질문 유형 분석 결과: $classification")
        mutableMapOf("question_type" to classification) as Map<String, Any>?
        mutableMapOf("query" to query) as Map<String, Any>?
    }

    val questionTypeEdge = edge_async<RagState> { state ->
        val classification = state.questionType()

        Log.d("LangGraph", "🔍 LLM의 질문 분류 결과: $classification")

        return@edge_async if (classification == "RAG") {
            "ingestNode"
        } else {
            "directAnswerNode"
        }
    }

    val directAnswerNode = node_async<RagState> { state, _ ->
        val query = state.query()
        Log.d(TAG, "🤔 직접 응답 생성 중: $query")

        val response = runBlocking {
            customChatModel.chatSync(query)
        }
        Log.d(TAG, "✅ 직접 응답 완료: $response")
        mutableMapOf("response" to response) as Map<String, Any>?
    }

    // 문서 벡터화 및 저장 (RAG Ingest)
    val ingestNode = node_async<RagState> { state, _ ->
        Log.d(TAG, "🚀 ingestNode가 호출되었습니다. : ${state.data()}")

        val filePaths = state.filePaths()
        if (filePaths.isEmpty()) {
            throw IllegalStateException("❌ ingestNode 실행 실패: filePaths 값이 없습니다.")
        }
        Log.d(TAG, "🚀 ingestNode 시작: $filePaths")

        val result = runBlocking { ragManager.updateExternalData(filePaths) }

        Log.d(TAG, "✅ 상태 저장 완료: $result")
        mutableMapOf("status" to result) as Map<String, Any>?
    }

    // 검색 및 벡터 유사도 계산 (RAG Search)
    val searchNode = node_async<RagState> { state, _ ->
        val query = state.query()
        Log.d(TAG, "🔍 검색 시작: $query")

        if (query.isEmpty()) {
            throw IllegalStateException("❌ 검색 실행 실패: query 값이 없습니다.")
        }

        return@node_async runBlocking {
            if (customEmbeddedModel.loadOnnxEmbeddedModel()) {
                val retriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingModel(customEmbeddedModel)
                    .embeddingStore(customVectorStore)
                    .maxResults(5)
                    .build()

                val searchResult = retriever.retrieve(Query.from(query))
                    .joinToString("\n") { it.textSegment().text() }

                Log.d(TAG, "✅ 검색 완료 - 저장된 결과: $searchResult")

                mutableMapOf("search_result" to searchResult) as Map<String, Any>?
            } else {
                Log.e(TAG, "❌ 검색 실패 - ONNX 모델 로딩 안됨")
                mutableMapOf("search_result" to "failed") as Map<String, Any>?
            }
        }
    }

    // LLM을 호출하여 최종 응답 생성
    val generateResponseNode = node_async<RagState> { state, _ ->
        val userQuery = state.query()
        val context = state.searchResult()

        Log.d(TAG, "💡 generateResponseNode 실행 - 사용자 입력: $userQuery")

        val response = runBlocking {
            customChatModel.chatSync("$userQuery\n\n$context")
        }

        Log.d(TAG, "✅ LLM 응답 저장 완료: $response")
        mutableMapOf("response" to response) as Map<String, Any>?
    }

    // 메시지 라우팅 (검색 결과에 따라 다음 노드 결정)
    val routeMessage = edge_async<RagState> { state ->
        val response = state.response()
        return@edge_async if (response.isEmpty() || response == "No response generated") {
            "exit" // 응답 없음 → 종료
        } else {
            "evaluateResultNode" // 정상 응답 → 평가 노드로 이동
        }
    }

    val evaluateResultNode = node_async<RagState> { state, _ ->
        val response = state.response()
        Log.d(TAG, "🔍 응답 평가 중: $response")

        emptyMap()
    }

    val exitNode = node_async<RagState> { state, _ ->
        Log.d(TAG, "🛑 프로세스 종료")
        emptyMap()
    }

    // LangGraph4j 상태 머신 정의
    val workflow: CompiledGraph<RagState> = StateGraph(RagState.SCHEMA, ::RagState)
        .addNode("llmNode", llmNode)
        .addNode("ingestNode", ingestNode)
        .addNode("searchNode", searchNode)
        .addNode("generateResponseNode", generateResponseNode)
        .addNode("directAnswerNode", directAnswerNode)
        .addNode("evaluateResultNode", evaluateResultNode)
        .addNode("exit", exitNode)

        .addEdge(StateGraph.START, "llmNode")
        .addConditionalEdges(
            "llmNode",
            questionTypeEdge,
            mapOf(
                "ingestNode" to "ingestNode",
                "directAnswerNode" to "directAnswerNode"
            )
        )
        .addEdge("ingestNode", "searchNode")
        .addEdge("searchNode", "generateResponseNode")
        .addConditionalEdges(
            "generateResponseNode",
            routeMessage,
            mapOf(
                "evaluateResultNode" to "evaluateResultNode",
                "exit" to "exit"
            )
        )
        .addConditionalEdges(
            "directAnswerNode",
            routeMessage,
            mapOf(
                "evaluateResultNode" to "evaluateResultNode",
                "exit" to "exit"
            )
        )
        .addEdge("evaluateResultNode", StateGraph.END)
        .addEdge("exit", StateGraph.END)
        .compile()

    suspend fun runRAGPipeline(filePaths: List<String>, query: String): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "🚀 runRAGPipeline 시작 - 파일 리스트: $filePaths, 쿼리: $query")

            val initialConfig = RunnableConfig.builder()
                .threadId("RAG_PIPELINE")
                .nextNode("llmNode")
                .build()

            val initialState = RagState(
                mapOf(
                    "filePaths" to filePaths,
                    "query" to query
                )
            )

            Log.d(TAG, "Graph 구조 : ${workflow.nodes.keys}")
            workflow.invoke(initialState.data(), initialConfig)

            Log.d(TAG, "✅ 워크플로우 실행 완료")
            val response = initialState.response()
            Log.d(TAG, "🚀 최종 응답: $response")

            return@withContext mapOf("query" to query, "response" to response)
        }
    }
}

