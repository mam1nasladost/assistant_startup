package com.example.assistant_startup.domain.repository

import kotlinx.coroutines.flow.Flow

interface AssistantRepo {
    fun getChatResponse(text: String): Flow<String>
}