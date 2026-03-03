package com.example.assistant_startup.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class LongMessage(
    val body: String,
    val id: Int,
    val title: String,
)
