package com.example.assistant_startup.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ShortMessage(
    val body: String,
    val title: String,
    val id: Int
)
