package com.example.assistant_startup.ui.features.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavController
import com.example.assistant_startup.domain.models.ChatUiState
import com.example.assistant_startup.domain.models.Message
import com.example.assistant_startup.domain.models.SearchResult
import com.example.assistant_startup.ui.components.getSnippetAtIndex
import com.example.assistant_startup.ui.components.highlightQuery
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String?,
    viewModel: ChatViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val previousRoute = navController.previousBackStackEntry?.destination?.route

    ChatContent(
        uiState = uiState,
        listState = listState,
        onSearchChange = viewModel::updateSearchQuery,
        onInputChange = viewModel::updateInputText,
        onSend = {},
        onBackClick = {
            val destination = if (previousRoute == "chats") "chats" else "home"
            navController.navigate(destination) {
                popUpTo(destination) { inclusive = true }
                launchSingleTop = true
            }
        },
        onSearchResultClick = { result ->
            viewModel.selectSearchResult(result)

            val index = uiState.messages.indexOfFirst { it.id == result.message.id }
            if (index != -1) {
                scope.launch {
                    listState.animateScrollToItem(index, scrollOffset = -200)
                }
            }
        }
    )
}

@Composable
fun ChatContent(
    uiState: ChatUiState,
    listState: LazyListState,
    onSearchChange: (String) -> Unit,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onBackClick: () -> Unit,
    onSearchResultClick: (SearchResult) -> Unit
) {
    val imeInsets = WindowInsets.ime.asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            state = listState,
            contentPadding = PaddingValues(
                top = 90.dp,
                bottom = 100.dp + imeInsets.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                ChatMessage(
                    message = message,
                    searchQuery = uiState.searchQuery,
                    selectedResult = uiState.selectedResult
                )
            }
        }

        ExpandableChatSearchBar(
            searchQuery = uiState.searchQuery,
            searchResults = uiState.searchResults,
            onSearchQueryChange = onSearchChange,
            onBackClick = onBackClick,
            onResultClick = onSearchResultClick,
        )

//        ChatInput(
//            value = uiState.inputText,
//            onValueChange = onInputChange,
//            onSend = onSend,
//            modifier = Modifier.align(Alignment.BottomCenter)
//        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableChatSearchBar(
    searchQuery: String,
    searchResults: List<SearchResult>,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onResultClick: (SearchResult) -> Unit,
) {
    var isActive by remember { mutableStateOf(false) }

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
            .clip(RoundedCornerShape(32.dp))
            .padding(4.dp),
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
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
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
                items(
                    items = searchResults,
                    key = { "${it.message.id}_${it.matchIndex}" }
                ) { result ->
                    SearchResultItem(
                        result = result,
                        onClick = {
                            isActive = false
                            onResultClick(result)
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            text = if (result.message.isUser) "Вы" else "Собеседник",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            text = result.message.text.getSnippetAtIndex(result.matchIndex, result.query),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2
        )
    }
}
