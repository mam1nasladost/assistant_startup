package com.example.assistant_startup.remote.repository_imp

import com.example.assistant_startup.domain.repository.AssistantRepo
import com.example.assistant_startup.remote.KtorClient
import kotlinx.coroutines.flow.Flow

class AssistantRepoImp(private val client: KtorClient): AssistantRepo {
    override fun getChatResponse(text: String): Flow<String> {
        return client.streamDeepSeek(text)
    }
}