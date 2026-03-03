package com.example.assistant_startup.ui.features.setting
import androidx.lifecycle.ViewModel
import com.example.assistant_startup.domain.models.SettingsUiState
import com.example.assistant_startup.domain.models.Trigger
import com.example.assistant_startup.domain.repository.SettingsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(private val repo: SettingsRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateAutoOff(isAutoOff: Boolean) {
        _uiState.update { it.copy(isAutoOff = isAutoOff, configIsChanged = true) }
    }

    fun updatePauseLength(length: Float) {
        _uiState.update { it.copy(pauseLength = length, configIsChanged = true) }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun addEndTrigger() {
        val text = _uiState.value.inputText.trim()
        if (text.isNotBlank()) {
            val currentTriggers = _uiState.value.endTriggers
            val exists = currentTriggers.any { it.name.equals(text, ignoreCase = true) }

            if (!exists) {
                _uiState.update { state ->
                    state.copy(
                        endTriggers = state.endTriggers + Trigger(text, true),
                        inputText = "",
                        configIsChanged = true
                    )
                }
            }
        }
    }

    fun removeEndTrigger(trigger: Trigger) {
        _uiState.update { state ->
            state.copy(
                endTriggers = state.endTriggers.filter { it.name != trigger.name },
                configIsChanged = true
            )
        }
    }

    fun toggleEndTrigger(trigger: Trigger, isOn: Boolean) {
        _uiState.update { state ->
            val updatedTriggers = state.endTriggers.map {
                if (it.name == trigger.name) it.copy(isOn = isOn) else it
            }
            state.copy(endTriggers = updatedTriggers, configIsChanged = true)
        }
    }

    fun addStartTrigger(name: String) {
        val trimmedName = name.trim()
        val currentTriggers = _uiState.value.startTriggers
        val exists = currentTriggers.any { it.name.equals(trimmedName, ignoreCase = true) }

        if (!exists) {
            _uiState.update { state ->
                state.copy(
                    startTriggers = state.startTriggers + Trigger(trimmedName, true),
                    configIsChanged = true
                )
            }
        }
    }

    fun removeStartTrigger(trigger: Trigger) {
        _uiState.update { state ->
            state.copy(
                startTriggers = state.startTriggers.filter { it.name != trigger.name },
                configIsChanged = true
            )
        }
    }

    fun toggleStartTrigger(trigger: Trigger, isOn: Boolean) {
        _uiState.update { state ->
            val updatedTriggers = state.startTriggers.map {
                if (it.name == trigger.name) it.copy(isOn = isOn) else it
            }
            state.copy(startTriggers = updatedTriggers, configIsChanged = true)
        }
    }

    fun applyChanges() {
        _uiState.update { it.copy(configIsChanged = false) }
    }
}