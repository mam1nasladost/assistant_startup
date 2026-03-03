package com.example.assistant_startup.ui.features.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.example.assistant_startup.domain.models.Message
import com.example.assistant_startup.domain.models.SearchResult
import com.example.assistant_startup.ui.components.highlightQuery


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessage(
    message: Message,
    searchQuery: String,
    selectedResult: SearchResult? = null
) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val messageColor = if (message.isUser) Color(0xFF3BB9E5) else Color(0xFFF0F2F5)
    val textColor = if (message.isUser) Color.White else Color(0xFF1C1B1F)

    val modifier = Modifier
        .padding(vertical = 2.dp)
        .fillMaxWidth(0.85f)

    val shape = if (message.isUser) {
        RoundedCornerShape(18.dp, 18.dp, 2.dp, 18.dp)
    } else {
        RoundedCornerShape(18.dp, 18.dp, 18.dp, 2.dp)
    }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    LaunchedEffect(selectedResult, textLayoutResult) {
        if (selectedResult != null &&
            selectedResult.message.id == message.id &&
            textLayoutResult != null
        ) {
            val cursorRect = textLayoutResult?.getBoundingBox(selectedResult.matchIndex)
            if (cursorRect != null) {
                bringIntoViewRequester.bringIntoView(cursorRect)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoViewRequester),
        contentAlignment = alignment
    ) {
        Surface(
            modifier = modifier,
            color = messageColor,
            shape = shape,
        ) {
            Text(
                text = message.text.highlightQuery(searchQuery, highlightColor = Color(0xFFFFF59D)),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                onTextLayout = { textLayoutResult = it }
            )
        }
    }
}