package com.example.assistant_startup.domain.repository

import com.example.assistant_startup.domain.models.Message

interface ChatRepo {
    fun getMessages(): List<Message>
}