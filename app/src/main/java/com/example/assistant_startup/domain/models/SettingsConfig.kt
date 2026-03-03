package com.example.assistant_startup.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SettingsConfig(
    val isAutoOff: Boolean = true,
    val pauseLength: Float = 5f,
    val endTriggers: List<Trigger> = listOf(Trigger("Стоп", true), Trigger("Закончил", false)),
    val startTriggers: List<Trigger> = listOf(Trigger("Риски", true), Trigger("Предложения", false)),
    val startTriggersOptions: List<String> = listOf("Риски", "Лимиты", "События", "Цена")
)
