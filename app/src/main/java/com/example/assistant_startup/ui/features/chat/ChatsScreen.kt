package com.example.assistant_startup.ui.features.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

data class AiQuery(
    val title: String,
    val message: String,
    val timestamp: String,
    val isFavorite: Boolean = false
)

val dummyData = listOf(
    AiQuery("Оптимизация SQL", "Попробуй добавить индекс на поле user_id", "14:20", true),
    AiQuery("Рецепт блинов", "Лучше добавить кипяток в тесто для ажурности.", "13:45", false),
    AiQuery("Дом на колесах", "Да, это замечательная идея, особенно для фриланса.", "12:45", false),
    AiQuery("Стихотворение маме", "Среди цветов, в сиянье дня, ты ярче всех сияешь между...", "11:30", false),
    AiQuery("Ошибка в Python", "Это случается из-за изменяемых аргументов по умолчанию.", "10:15", false),
    AiQuery("План тренировок", "На сегодня: 3 подхода приседаний и планка.", "09:05", false),
    AiQuery("Перевод фразы", "The idiom 'break a leg' means 'good luck'.", "Вчера", false),
    AiQuery("Выбор ноутбука", "Для Compose лучше брать минимум 16 ГБ ОЗУ.", "Вчера", false),
)

@Composable
fun ChatsScreen(navController: NavController, showFavorites: Boolean) {
    ChatContent(
        onBackClick = {
            navController.navigate("home") {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        onChatClick = { title ->
            navController.navigate("chat/${title}")
        },
        showFavorites = showFavorites
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(onBackClick: () -> Unit, onChatClick: (String) -> Unit, showFavorites: Boolean) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredData = remember(searchQuery, showFavorites) {
        val baseList = if (searchQuery.isEmpty()) {
            dummyData
        } else {
            dummyData.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.message.contains(searchQuery, ignoreCase = true)
            }
        }
        if (showFavorites) {
            baseList.filter { it.isFavorite }
        } else {
            baseList
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 100.dp, bottom = 16.dp, start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredData) { chat ->
                ChatCard(
                    name = chat.title,
                    lastMessage = chat.message,
                    time = chat.timestamp,
                    onClick = {onChatClick(chat.title)},
                    isFavorite = chat.isFavorite
                )
            }
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Поиск сообщения...") },
            leadingIcon = {
                IconButton(
                    onClick = { onBackClick() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                }
            },
            colors = SearchBarDefaults.colors(
                containerColor = Color.White,
                dividerColor = Color.Gray.copy(alpha = 0.5f)
            ),
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            active = false,
            onActiveChange = {},
        ) {}
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChatScreenPreview() {
    ChatContent(onBackClick = {}, onChatClick = {}, showFavorites = false)
}