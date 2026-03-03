package com.example.assistant_startup.ui.features.chat

import androidx.lifecycle.ViewModel
import com.example.assistant_startup.domain.models.ChatUiState
import com.example.assistant_startup.domain.models.Message
import com.example.assistant_startup.domain.models.SearchResult
import com.example.assistant_startup.domain.repository.ChatRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel(private val repo: ChatRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    fun loadMessages() {
        val messages: List<Message> = repo.getMessages()

        _uiState.update { it.copy(messages = messages)}
    }

    fun selectSearchResult(result: SearchResult) {
        _uiState.update { it.copy(selectedResult = result) }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            val results = if (query.isBlank()) {
                emptyList()
            } else {
                val allMatches = mutableListOf<SearchResult>()

                state.messages.forEach { message ->
                    var index = message.text.indexOf(query, ignoreCase = true)
                    while (index != -1) {
                        allMatches.add(
                            SearchResult(
                                message = message,
                                matchIndex = index,
                                query = query
                            )
                        )
                        index = message.text.indexOf(query, index + query.length, ignoreCase = true)
                    }
                }
                allMatches
            }

            state.copy(
                searchQuery = query,
                searchResults = results,
                selectedResult = null
            )
        }
    }

//    fun sendMessage() {
//        val text = _uiState.value.inputText.trim()
//        if (text.isNotBlank()) {
//            _uiState.update { state ->
//                val newMessage = Message(text = text, isUser = true)
//                state.copy(
//                    messages = state.messages + newMessage,
//                    inputText = ""
//                )
//            }
//        }
//    }
}