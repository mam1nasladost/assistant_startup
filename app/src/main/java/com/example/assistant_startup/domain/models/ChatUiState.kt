package com.example.assistant_startup.domain.models

import java.util.UUID

data class Message(
    val text: String,
    val isUser: Boolean,
    val id: String = UUID.randomUUID().toString()
)

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val selectedResult: SearchResult? = null
)
