package com.example.rag.store

import android.util.Log
import com.example.database.objectbox.ObjectBoxStore
import com.example.database.objectbox.data.Segment
import com.example.database.objectbox.data.Segment_
import com.example.rag.TAG
import dev.langchain4j.data.embedding.Embedding
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.internal.Utils
import dev.langchain4j.store.embedding.EmbeddingMatch
import dev.langchain4j.store.embedding.EmbeddingSearchRequest
import dev.langchain4j.store.embedding.EmbeddingSearchResult
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.filter.Filter
import dev.langchain4j.data.document.Metadata
import java.io.IOException
import java.util.stream.Collectors
import javax.inject.Inject

class CustomVectorStore @Inject constructor(
): EmbeddingStore<TextSegment> {

    private val segmentBox = ObjectBoxStore.store.boxFor(Segment::class.java)

    override fun add(
        embedding: Embedding
    ): String {
        val id = Utils.randomUUID()
        add(id, embedding)
        return id
    }

    override fun add(
        id: String,
        embedding: Embedding
    ) {
        addInternal(listOf(id), listOf(embedding), emptyList())
    }

    override fun add(
        embedding: Embedding,
        textSegment: TextSegment
    ): String {
        val id = Utils.randomUUID()
        addInternal(listOf(id), listOf(embedding), listOf(textSegment))
        return id
    }

    override fun addAll(
        embeddings: List<Embedding>
    ): List<String> {
        val ids = embeddings.stream()
            .map { Utils.randomUUID() }
            .collect(Collectors.toList())
        addInternal(ids, embeddings, emptyList())
        return ids
    }

    override fun addAll(
        embeddings: List<Embedding>,
        textSegments: List<TextSegment>
    ): List<String> {
        val ids = embeddings.stream()
            .map { Utils.randomUUID() }
            .collect(Collectors.toList())
        addInternal(ids, embeddings, textSegments)
        return ids
    }

    private fun addInternal(
        ids: List<String>,
        embeddings: List<Embedding>,
        textSegments: List<TextSegment>
    ) {
        val segments = mutableListOf<Segment>()

        try {
            ids.forEachIndexed { index, id ->
                val embedding = embeddings.getOrNull(index)
                val textSegment = textSegments.getOrNull(index)

                if (embedding == null || textSegment == null) {
                    return@forEachIndexed
                } else {
                    segments.add(
                        Segment(
                            uuid = id,
                            text = textSegment.text(),
                            vector = embedding.vector()
                        )
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (segments.isNotEmpty()) {
            segmentBox.put(segments)
        }
    }

    override fun removeAll(
        ids: Collection<String>
    ) {
        segmentBox.removeByIds(
            ids.map { it.toLong() }
        )
    }

    override fun removeAll(
        filter: Filter
    ) {
        segmentBox.all.removeIf { segment ->
            filter.test(segment.id)
        }
    }

    override fun removeAll() {
        segmentBox.removeAll()
    }

    override fun search(
        request: EmbeddingSearchRequest
    ): EmbeddingSearchResult<TextSegment> {
        val queryEmbedding = request.queryEmbedding().vector()
        val maxResults = request.maxResults()

        // HNSW 검색 수행
        val similarSegments = segmentBox
            .query(Segment_.vector.nearestNeighbors(queryEmbedding, maxResults))
            .build()
            .findWithScores()

        val matches = similarSegments.map { result ->
            val segment = result.get()
            val metadata = Metadata.from(
                mapOf(
                    "uuid" to segment.uuid,
                    "source" to "objectbox"
                )
            )
            EmbeddingMatch(
                result.score,
                segment.uuid,
                Embedding(segment.vector),
                TextSegment(segment.text, metadata)
            )
        }

        matches.forEach {
            Log.i(TAG, "search result : ${it.score()} // ${it.embedded().text()}")
        }

        return EmbeddingSearchResult(matches)
    }
}

