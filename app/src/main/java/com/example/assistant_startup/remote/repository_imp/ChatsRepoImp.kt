package com.example.assistant_startup.remote.repository_imp

import com.example.assistant_startup.domain.repository.ChatsRepo
import com.example.assistant_startup.remote.KtorClient

class ChatsRepoImp(private val client: KtorClient): ChatsRepo {
}