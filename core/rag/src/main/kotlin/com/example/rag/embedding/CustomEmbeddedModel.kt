package com.example.rag.embedding

import android.util.Log
import com.example.rag.TAG
import com.example.rag.util.Utils
import com.ml.shubham0204.sentence_embeddings.SentenceEmbedding
import dev.langchain4j.data.embedding.Embedding
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.output.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class CustomEmbeddedModel @Inject constructor(
): EmbeddingModel {

    private val sentenceEmbedding by lazy { SentenceEmbedding() }

    /**
     * 임베딩
     * 모델 경로는 임의로 설정
     *
     * ex) data/local/tmp/emb/ ~
     */
    suspend fun loadOnnxEmbeddedModel() : Boolean = runCatching {
        initSentenceEmbedding(
            onnxFilePath = Utils.copyAndReturnPath("data/local/tmp/emb/model_quantized.onnx"),   // ex) 파일 경로 설정 필요
            tokenizerByteArray = Utils.copyAndReturnBytes("data/local/tmp/emb/tokenizer.json")   // ex) 파일 경로 설정 필요
        )
    }.onFailure {
        Log.e(TAG, "loadOnnxEmbeddedModel fatal $it")
    }.getOrDefault(false)

    suspend fun initSentenceEmbedding(
        onnxFilePath: String,
        tokenizerByteArray: ByteArray,
    ): Boolean {
        val result = runCatching {
            Log.i(TAG, "embeddings init start")
            sentenceEmbedding.init(
                modelFilepath = onnxFilePath,
                tokenizerBytes = tokenizerByteArray,
                useTokenTypeIds = false,
                outputTensorName = "last_hidden_state",
                normalizeEmbeddings = true
            )
            Log.i(TAG, "embeddings init stop")
            return@runCatching true
        }.onFailure {
            Log.i(TAG, "")
        }.onSuccess {
            Log.i(TAG, "")
        }.getOrDefault(false)

        return result
    }

    override fun embedAll(
        textSegments: List<TextSegment>
    ): Response<List<Embedding>> {
        val response = runBlocking(Dispatchers.Default) {
            val embeddings = mutableListOf<Embedding>()

            textSegments.forEach { segment ->
                val vector = sentenceEmbedding.encode(segment.text())
                val embedding = Embedding(vector)
                embeddings.add(embedding)
            }

            Response(embeddings.toList())
        }
        return response
    }

    suspend fun close() {
        sentenceEmbedding.close()
    }
}