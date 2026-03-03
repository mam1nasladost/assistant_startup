package com.example.assistant_startup.domain.models

data class SettingsUiState(
    val isAutoOff: Boolean = true,
    val pauseLength: Float = 5f,
    val inputText: String = "",
    val endTriggers: List<Trigger> = listOf(Trigger("Стоп", true), Trigger("Закончил", false)),
    val startTriggers: List<Trigger> = listOf(Trigger("Риски", true), Trigger("Предложения", false)),
    val configIsChanged: Boolean = false,
    val startTriggersOptions: List<String> = listOf("Риски", "Лимиты", "События", "Цена")
)