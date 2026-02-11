package com.example.assistant_startup.ui.features.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Message(val text: String, val isUser: Boolean)

@Composable
fun ChatMessage(message: Message) {
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

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            modifier = modifier,
            color = messageColor,
            shape = shape,
//            shadowElevation = 1.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = textColor,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview
@Composable
fun MessagesPreview() {
    val messages = listOf(Message("Hi, its me chatGPT", false), Message("Hi, how are you?", true))

    Column{
        ChatMessage(messages[0])
        ChatMessage(messages[1])
    }
}