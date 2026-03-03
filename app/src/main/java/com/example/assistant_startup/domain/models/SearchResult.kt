package com.example.assistant_startup.domain.models

data class SearchResult(
    val message: Message,
    val matchIndex: Int,
    val query: String
)
