package com.example.assistant_startup.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: String,
    val isFavorite: Boolean = false
)
