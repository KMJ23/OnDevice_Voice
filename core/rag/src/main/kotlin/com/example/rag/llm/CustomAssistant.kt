package com.example.rag.llm

import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import dev.langchain4j.service.TokenStream

// TODO: LLM 동작 구현 필요

private const val SYSTEM_TEMPLATE = """
    You are an AI writing assistant.
"""

private const val USER_TEMPLATE = """
"""

interface Assistant {
    @SystemMessage(SYSTEM_TEMPLATE)
    //@UserMessage(USER_TEMPLATE)
    fun chat(userMessage: String): TokenStream
}