package com.example.assistant_startup.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Trigger(
    val name: String,
    val isOn: Boolean
)
