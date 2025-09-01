package com.example.rag

import android.util.Log
import com.example.database.room.DummyDao
import com.example.database.room.data.DummyEntity
import com.example.rag.embedding.CustomEmbeddedModel
import com.example.rag.llm.Assistant
import com.example.rag.llm.CustomChatModel
import com.example.rag.store.CustomVectorStore
import com.example.rag.util.Utils.loadToExcel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.measureTimeMillis


internal const val TAG = "ondevice_rag"

@ActivityRetainedScoped
class RagManager @Inject constructor(
    private val customChatModel: CustomChatModel,
    private val customEmbeddedModel: CustomEmbeddedModel,
    private val customVectorStore: CustomVectorStore,
    private val dummyDao: DummyDao // TODO: TEST Room Database
) {

    suspend fun updateLocalData(): Boolean {
        val result = withContext(Dispatchers.Default) {
            if (customEmbeddedModel.loadOnnxEmbeddedModel()) {
                dummyDao.insert(dummyDatas)

                customVectorStore.removeAll()

                val document = Document(dummyDao.getAll().toString())
                val ingestor =
                    EmbeddingStoreIngestor.builder()
                        .documentSplitter(DocumentSplitters.recursive(200, 50))
                        .embeddingModel(customEmbeddedModel)
                        .embeddingStore(customVectorStore)
                        .build()

                ingestor.ingest(document)
                true
            } else {
                Log.e(TAG, "failed to load onnx model")
                false
            }
        }

        return result
    }

    suspend fun updateExternalData(filePaths: List<String>): Boolean {
        var result: Boolean
        measureTimeMillis {
             result = withContext(Dispatchers.Default) {
                if (customEmbeddedModel.loadOnnxEmbeddedModel()) {
                    customVectorStore.removeAll()

                    val document = loadToExcel("data/local/tmp/file/tvChannels.xlsx")
                    val ingestor =
                        EmbeddingStoreIngestor.builder()
                            .documentSplitter(DocumentSplitters.recursive(200, 50))
                            .embeddingModel(customEmbeddedModel)
                            .embeddingStore(customVectorStore)
                            .build()

                    Log.i(TAG, "update start")
                    ingestor.ingest(document)
                    Log.i(TAG, "update stop")
                    true
                } else {
                    Log.e(TAG, "failed to load onnx model")
                    false
                }
            }
        }.also {
            Log.i(TAG, "measure update time: $it ms")
        }

        return result
    }

    suspend fun search(
        query: String
    ): String {
        var answer: String
        measureTimeMillis {
            answer = withContext(Dispatchers.Default) {
                if (customEmbeddedModel.loadOnnxEmbeddedModel()) {
                    val aiServices = AiServices.builder(Assistant::class.java)
                        .streamingChatLanguageModel(customChatModel)
                        .contentRetriever(
                            EmbeddingStoreContentRetriever.builder()
                                .embeddingModel(customEmbeddedModel)
                                .embeddingStore(customVectorStore)
                                .maxResults(5)
                                .build()
                        )
                        //.chatMemory(MessageWindowChatMemory.withMaxMessages(5))
                        .build()

                    val text = StringBuilder()

                    Log.i(TAG, "search start")
                    with(aiServices.chat(query)) {
                        onNext {
                            text.append(it)
                            Log.e(TAG, "onNext $text")
                        }
                        onComplete {
                            Log.e(TAG, "onComplete $it")
                        }
                        onError {
                            text.clear()
                            Log.e(TAG, "onError $it")
                        }
                        start()
                    }
                    Log.i(TAG, "search stop")
                    text.toString()
                } else {
                    Log.e(TAG, "failed to load onnx model")
                    "failed"
                }
            }
        }.also {
            Log.i(TAG, "measure search time: $it ms")
        }

        customEmbeddedModel.close()
        customChatModel.close()

        return answer
    }
}


// TODO: TEST Room Database Entity
internal val dummyDatas = listOf(
    DummyEntity (
        watchingStartTime = "2025-01-06 08:00",
        watchingEndTime = "2025-01-06 9:00",
        broadcast = "MBC",
        program = "라디오스타",
        channelNumber = "101",
        channelId = "12345"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 09:10",
        watchingEndTime = "2025-01-06 10:10",
        broadcast = "SBS",
        program = "미운 우리 새끼",
        channelNumber = "102",
        channelId = "23456"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 10:20",
        watchingEndTime = "2025-01-06 11:20",
        broadcast = "KBS1",
        program = "6시 내고향",
        channelNumber = "103",
        channelId = "34567"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 11:30",
        watchingEndTime = "2025-01-06 12:30",
        broadcast = "KBS2",
        program = "생생정보통",
        channelNumber = "104",
        channelId = "45678"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 12:40",
        watchingEndTime = "2025-01-06 13:40",
        broadcast = "EBS",
        program = "세계테마기행",
        channelNumber = "105",
        channelId = "56789"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 13:50",
        watchingEndTime = "2025-01-06 14:50",
        broadcast = "YTN",
        program = "뉴스 출발",
        channelNumber = "106",
        channelId = "67890"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 15:00",
        watchingEndTime = "2025-01-06 16:00",
        broadcast = "JTBC",
        program = "아는 형님",
        channelNumber = "107",
        channelId = "78901"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 16:10",
        watchingEndTime = "2025-01-06 17:10",
        broadcast = "tvN",
        program = "슬기로운 의사생활",
        channelNumber = "108",
        channelId = "89012"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 17:20",
        watchingEndTime = "2025-01-06 18:20",
        broadcast = "MBN",
        program = "나는 자연인이다",
        channelNumber = "109",
        channelId = "90123"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 18:30",
        watchingEndTime = "2025-01-06 19:30",
        broadcast = "채널A",
        program = "서민갑부",
        channelNumber = "110",
        channelId = "01234"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 19:40",
        watchingEndTime = "2025-01-06 20:40",
        broadcast = "연합뉴스TV",
        program = "뉴스 특보",
        channelNumber = "111",
        channelId = "12346"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 20:50",
        watchingEndTime = "2025-01-06 21:50",
        broadcast = "OBS",
        program = "OBS 뉴스",
        channelNumber = "112",
        channelId = "23457"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 22:00",
        watchingEndTime = "2025-01-06 23:00",
        broadcast = "아리랑TV",
        program = "Korea Today",
        channelNumber = "113",
        channelId = "34568"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-06 23:10",
        watchingEndTime = "2025-01-07 00:10",
        broadcast = "KTV",
        program = "정책 브리핑",
        channelNumber = "114",
        channelId = "45679"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-07 00:20",
        watchingEndTime = "2025-01-07 01:20",
        broadcast = "국회방송",
        program = "국회 본회의",
        channelNumber = "115",
        channelId = "56780"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-07 01:30",
        watchingEndTime = "2025-01-07 02:30",
        broadcast = "KBS N 스포츠",
        program = "야구 중계",
        channelNumber = "116",
        channelId = "67891"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-07 02:40",
        watchingEndTime = "2025-01-07 03:40",
        broadcast = "MBC 스포츠+",
        program = "축구 중계",
        channelNumber = "117",
        channelId = "78902"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-07 03:50",
        watchingEndTime = "2025-01-07 04:50",
        broadcast = "SBS 스포츠",
        program = "테니스 중계",
        channelNumber = "118",
        channelId = "89013"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-07 05:00",
        watchingEndTime = "2025-01-07 06:00",
        broadcast = "SPOTV",
        program = "UFC 중계",
        channelNumber = "119",
        channelId = "90124"
    ),
    DummyEntity (
        watchingStartTime = "2025-01-07 06:10",
        watchingEndTime = "2025-01-07 07:10",
        broadcast = "SPOTV2",
        program = "배구 중계",
        channelNumber = "120",
        channelId = "01235"
    )
)