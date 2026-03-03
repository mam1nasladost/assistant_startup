package com.example.assistant_startup.ui.features.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AssistantMessage(text: String) {
    val scrollState = rememberScrollState()

    // Автоматический скролл вниз при добавлении нового текста
    LaunchedEffect(text.length) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(scrollState) // Позволяет прокручивать длинные ответы
    ) {
        Text(
            text = "Assistant:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (text.isEmpty()) "Думаю..." else text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (text.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
        )
    }
}