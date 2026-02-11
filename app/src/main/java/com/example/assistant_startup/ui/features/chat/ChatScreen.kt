package com.example.assistant_startup.ui.features.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(navController: NavController, chatId: String?) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val previousRoute = navController.previousBackStackEntry?.destination?.route

    val messages = remember {
        mutableStateListOf(
            Message(
                "Привет! Поможешь разобраться с SQL? Мне нужно вытащить данные из двух таблиц.",
                isUser = true
            ),
            Message(
                "Конечно! С радостью помогу. Какие у тебя таблицы и что именно нужно найти?",
                isUser = false
            ),
            Message(
                "Есть 'Orders' (заказы) и 'Users' (пользователи). Нужно найти топ-3 пользователей, которые потратили больше всего денег за последний месяц.",
                isUser = true
            ),
            Message(
                "Понял. Для этого нам понадобится INNER JOIN, фильтрация по дате через WHERE, группировка GROUP BY и сортировка. У тебя есть поле с датой и суммой заказа?",
                isUser = false
            ),
            Message("Да, поля называются 'created_at' и 'total_price'.", isUser = true),
            Message(
                "Отлично. Запрос будет выглядеть примерно так:\n\nSELECT u.name, SUM(o.total_price) as total\nFROM Users u\nJOIN Orders o ON u.id = o.user_id\nWHERE o.created_at >= DATE('now', '-30 days')\nGROUP BY u.id\nORDER BY total DESC\nLIMIT 3;",
                isUser = false
            ),
            Message("А что делает часть 'DATE('now', '-30 days')'?", isUser = true),
            Message(
                "Это функция SQLite для получения даты, которая была 30 дней назад от текущего момента. В PostgreSQL или MySQL синтаксис будет немного отличаться (например, INTERVAL '30 days').",
                isUser = false
            ),
            Message(
                "Работает! Но теперь я хочу видеть только тех, у кого средний чек выше 1000. Это тоже в WHERE добавить?",
                isUser = true
            ),
            Message(
                "Не совсем. Для фильтрации по результатам агрегатных функций (как SUM или AVG) нужно использовать HAVING после GROUP BY. Добавь: HAVING AVG(o.total_price) > 1000.",
                isUser = false
            ),
            Message(
                "Круто, теперь всё встало на свои места. Спасибо за объяснение!",
                isUser = true
            ),
            Message(
                "Всегда пожалуйста! Обращайся, если возникнут вопросы по вложенным запросам или индексам. Удачного кодинга!",
                isUser = false
            )
        )
    }

    ChatContent(
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        messages = messages,
        inputText = inputText,
        onInputChange = { inputText = it },
        onSend = {
            if (inputText.isNotBlank()) {
                messages.add(Message(inputText, isUser = true))
                inputText = ""
            }
        },
        onBackClick = {
            val destination = if (previousRoute == "chats") "chats" else "home"
            navController.navigate(destination) {
                popUpTo(destination) { inclusive = true }
                launchSingleTop = true
            }
        },
        listState = listState,
        onSearchResultClick = { foundMessage ->
            val index = messages.indexOf(foundMessage)
            if (index != -1) {
                coroutineScope.launch {
                    listState.animateScrollToItem(index)
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    messages: List<Message>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onBackClick: () -> Unit,
    listState: LazyListState,
    onSearchResultClick: (Message) -> Unit
) {
    val imeInsets = WindowInsets.ime.asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                top = 90.dp,
                bottom = 100.dp + imeInsets.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatMessage(message)
            }
        }

        ExpandableChatSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchChange,
            onBackClick = onBackClick,
            messages = messages,
            onResultClick = onSearchResultClick
        )

        ChatInput(
            value = inputText,
            onValueChange = onInputChange,
            onSend = onSend,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableChatSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    messages: List<Message>,
    onResultClick: (Message) -> Unit
) {
    var isActive by remember { mutableStateOf(false) }

    val searchResults = remember(searchQuery, messages) {
        if (searchQuery.isBlank()) emptyList()
        else messages.filter { it.text.contains(searchQuery, ignoreCase = true) }
    }

    BackHandler(enabled = isActive) {
        isActive = false
        onSearchQueryChange("")
    }

    SearchBar(
        query = searchQuery,
        onQueryChange = onSearchQueryChange,
        onSearch = { isActive = false },
        active = isActive,
        onActiveChange = {
            isActive = it
            if (!it) onSearchQueryChange("")
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp)),
        placeholder = { Text("Поиск сообщения...") },
        leadingIcon = {
            IconButton(
                onClick = {
                    if (isActive) {
                        isActive = false
                        onSearchQueryChange("")
                    } else {
                        onBackClick()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            } else if (!isActive) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = Color.White,
            dividerColor = Color.Gray.copy(alpha = 0.5f)
        ),
        tonalElevation = if (isActive) 0.dp else 2.dp,
        shadowElevation = if (isActive) 0.dp else 2.dp
    ) {
        if (searchResults.isEmpty() && searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Ничего не найдено", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { message ->
                    SearchResultItem(
                        message = message,
                        onClick = {
                            isActive = false
                            onResultClick(message)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(message: Message, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = if (message.isUser) "Вы" else "Собеседник",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    val demoMessages = listOf(
        Message("Это превью чата", isUser = false),
        Message("Круто, всё работает!", isUser = true)
    )

    ChatContent(
        searchQuery = "",
        onSearchChange = {},
        messages = demoMessages.reversed(),
        inputText = "Сообщение...",
        onInputChange = {},
        onSend = {},
        onBackClick = {},
        listState = rememberLazyListState(),
        onSearchResultClick = {}
    )
}