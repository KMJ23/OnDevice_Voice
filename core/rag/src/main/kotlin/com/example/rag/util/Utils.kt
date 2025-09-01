package com.example.rag.util

import android.util.Log
import com.example.rag.TAG
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.document.parser.TextDocumentParser
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter
import dev.langchain4j.data.segment.TextSegment
import java.io.File

object Utils {

    /**
     * 파일 로드
     * 파일 경로는 임의로 설정
     *
     * ex) data/local/tmp/file/ ~
     */
    fun loadToText(
        filepath: String
    ) = runCatching {
        FileSystemDocumentLoader.loadDocument(filepath, TextDocumentParser())
    }.onFailure {
        it.printStackTrace()
    }.getOrNull()

    fun loadToWord(
        filepath: String
    ) = runCatching {
        FileSystemDocumentLoader.loadDocument(filepath, ApachePoiDocumentParser())
    }.onFailure {
        it.printStackTrace()
    }.getOrNull()

    fun loadToExcel(
        filepath: String
    ) = runCatching {
        FileSystemDocumentLoader.loadDocument(filepath, ApachePoiDocumentParser())
    }.onFailure {
        it.printStackTrace()
    }.getOrNull()

    fun loadToPdf(
        filepath: String
    ): Document? {
        val pdfReader = PdfReader(filepath)

        val document = runCatching {
            val text = (1..pdfReader.numberOfPages)
                .joinToString("\n") { page ->
                    PdfTextExtractor.getTextFromPage(pdfReader, page)
                }

            Document(text)
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()

        pdfReader.close()

        return document
    }

    /**
     * 텍스트 분할
     *
     * 단락(DocumentByParagraphSplitter)
     * 라인(DocumentByLineSplitter)
     * 문장(DocumentBySentenceSplitter)
     * 단어(DocumentByWordSplitter)
     * 문자(DocumentByCharacterSplitter)
     * 정규식(DocumentByRegexSplitter)
     * 재귀적 분할(recursive)
     */
    fun documentSplit(
        document: Document
    ): List<TextSegment> = runCatching {
        val documentBySentenceSplitter = DocumentByLineSplitter(
            100,
            10
        )

        documentBySentenceSplitter.split(document)
    }.onFailure {
        Log.e(TAG, "Error in text splitting or embedding generation: $it")
    }.getOrDefault(emptyList())

    // Copy the file from the assets to the app's internal/private storage
    // and return its absolute path
    fun copyAndReturnPath(
        assetsFilepath: String
    ): String {
        val storageFile = File(assetsFilepath)
        return storageFile.absolutePath
    }

    // Copy the file from the assets to the app's internal/private storage
    // and return its data as a ByteArray
    fun copyAndReturnBytes(
        assetsFilepath: String
    ): ByteArray {
        val storageFile = File(assetsFilepath)
        return storageFile.readBytes()
    }
}