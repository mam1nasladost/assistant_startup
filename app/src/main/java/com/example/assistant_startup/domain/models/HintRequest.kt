package com.example.assistant_startup.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class HintRequest(
    val query: String,
    val context: String? = null
)
