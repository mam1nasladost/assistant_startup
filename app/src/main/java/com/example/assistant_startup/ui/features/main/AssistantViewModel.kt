package com.example.assistant_startup.ui.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import com.example.assistant_startup.domain.repository.AssistantRepo

class AssistantViewModel(private val repo: AssistantRepo) : ViewModel() {

    private val _assistantText = MutableStateFlow("")
    val assistantText: StateFlow<String> = _assistantText.asStateFlow()

    fun onSendMessage(userText: String) {
        viewModelScope.launch {
            Log.d("AssistantViewModel", "onSendMessage: $userText")
            _assistantText.value = ""

            repo.getChatResponse(userText)
                .catch { e ->
                    Log.e("AssistantViewModel", "Ошибка сети", e)
                    _assistantText.update { "$it\n[Ошибка соединения]" }
                }
                .collect { chunk ->
                    _assistantText.update { currentText -> currentText + chunk }
                }
        }
    }
}