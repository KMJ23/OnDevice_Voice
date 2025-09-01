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
): StreamingChatLanguageModel {

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
}