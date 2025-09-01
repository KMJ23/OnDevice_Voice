package com.example.rag.llm

import android.llama.cpp.LLamaAndroid
import android.util.Log
import com.example.rag.TAG
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.model.StreamingResponseHandler
import dev.langchain4j.model.chat.StreamingChatLanguageModel
import dev.langchain4j.model.output.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.system.measureTimeMillis

class CustomChatModel @Inject constructor(
):
StreamingChatLanguageModel {

    private val llamaAndroid by lazy { LLamaAndroid.instance() }

    private var chatJob: Job? = null

    /**
     * LLM
     * 모델 경로는 임의로 설정
     *
     * ex) data/local/tmp/llm/ ~
     */
    fun loadGGUFLlmModel(): Boolean = runCatching {
        // 임시 방편
        runBlocking {
            llamaAndroid.load("data/local/tmp/llm/SmolLM2-135M-Instruct.F16.gguf")
        }
        true
    }.onFailure {
        Log.e(TAG, "loadGGUFLlmModel fatal $it")
    }.onSuccess {
        Log.i(TAG, "loadGGUFLlmModel success")
    }.getOrDefault(false)


    override fun generate(
        messages: MutableList<ChatMessage>,
        handler: StreamingResponseHandler<AiMessage>
    ) {
        if (!llamaAndroid.isInitialized()) {
            loadGGUFLlmModel()
        }

        // TODO: 다음 LangChain4j 버전에서 삭제되는 코드
        // TODO: 버전 변경 시 검토 필요

        val query = messages.joinToString("\n") { it.text() }
        val answer = llamaAndroid.send(query)

        runBlocking {
            chatJob?.cancelAndJoin()
            chatJob = launch(Dispatchers.Default) {
                answer.collect {
                    try {
                        if (it == "<EOS>") {
                            handler.onComplete(Response(AiMessage((query))))
                        } else {
                            handler.onNext(it)
                        }
                    } catch (e: Exception) {
                        handler.onError(e)
                    }
                }
            }
        }
    }

    suspend fun close() {
        chatJob?.cancelAndJoin()
        chatJob = null
        // llamaAndroid.unload() TODO: 오류 발생
    }

    fun classifyQuery(query: String): String {
        if (!llamaAndroid.isInitialized()) {
            loadGGUFLlmModel()
        }
        val prompt = """
        You are an AI assistant. You must classify user questions into two types:
        🔝 **"direct"** → Questions that you can answer immediately (e.g., "What is the population of Seoul?")
        🔞 **"RAG"** → Questions that require searching user records or documents (e.g., "What channels have I watched the most?")
        📌 Question: "$query"  
        **Output only one word: "RAG" or "direct". No explanations.**
    """.trimIndent()
        val response = StringBuilder()
        runBlocking {
            chatJob?.cancelAndJoin()
            chatJob = launch(Dispatchers.IO) {
                try {
                    llamaAndroid.send(prompt).collect {
                        if (it != "<EOS>") {
                            response.append(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "🚨 Llama 실행 중 오류 발생: ${e.message}")
                }
            }
        }
        val result = response.toString().trim()
        return when {
            result.equals("RAG", ignoreCase = true) -> "RAG"
            result.equals("direct", ignoreCase = true) -> "direct"
            else -> {
                Log.w(TAG, "⚠ LLM이 예상하지 않은 응답을 반환함: $result")
                "direct"
            }
        }
    }

    fun chatSync(query: String): String {
        if (!llamaAndroid.isInitialized()) {
            loadGGUFLlmModel()
        }
        val response = StringBuilder()
        val time = measureTimeMillis {
            runBlocking(Dispatchers.IO) {
                try {
                    llamaAndroid.send(query).collect { token ->
                        if (token != "<EOS>") {
                            response.append(token)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "🚨 Llama execution error in chatSync: ${e.message}")
                }
            }
        }
        Log.i(TAG, "chatSync completed in $time ms")
        return response.toString().trim()
    }

}