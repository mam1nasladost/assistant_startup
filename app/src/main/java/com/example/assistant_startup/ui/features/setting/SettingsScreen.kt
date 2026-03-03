package com.example.assistant_startup.ui.features.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import com.example.assistant_startup.domain.models.SettingsUiState
import com.example.assistant_startup.domain.models.Trigger
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onBackClick = { navController.navigate("home") },
        onAutoOffChange = viewModel::updateAutoOff,
        onPauseLengthChange = viewModel::updatePauseLength,
        onInputTextChange = viewModel::updateInputText,
        onAddEndTrigger = viewModel::addEndTrigger,
        onRemoveEndTrigger = viewModel::removeEndTrigger,
        onToggleEndTrigger = viewModel::toggleEndTrigger,
        onAddStartTrigger = viewModel::addStartTrigger,
        onRemoveStartTrigger = viewModel::removeStartTrigger,
        onToggleStartTrigger = viewModel::toggleStartTrigger,
        onApplyChanges = {
            viewModel.applyChanges()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onAutoOffChange: (Boolean) -> Unit,
    onPauseLengthChange: (Float) -> Unit,
    onInputTextChange: (String) -> Unit,
    onAddEndTrigger: () -> Unit,
    onRemoveEndTrigger: (Trigger) -> Unit,
    onToggleEndTrigger: (Trigger, Boolean) -> Unit,
    onAddStartTrigger: (String) -> Unit,
    onRemoveStartTrigger: (Trigger) -> Unit,
    onToggleStartTrigger: (Trigger, Boolean) -> Unit,
    onApplyChanges: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Настройки",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    "Конец записи",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Автовыключение", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(uiState.isAutoOff, onCheckedChange = onAutoOffChange)
                }

                Column {
                    AnimatedVisibility(
                        visible = uiState.isAutoOff,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), MaterialTheme.shapes.medium)
                                .padding(vertical = 16.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "${uiState.pauseLength.toInt()} секунд",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Slider(
                                value = uiState.pauseLength,
                                onValueChange = onPauseLengthChange,
                                valueRange = 0f..10f,
                                steps = 9
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = !uiState.isAutoOff,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), MaterialTheme.shapes.medium)
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.inputText,
                                    onValueChange = onInputTextChange,
                                    label = { Text("Добавить триггер") },
                                    placeholder = { Text("Введите текст...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    leadingIcon = {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                    },
                                    trailingIcon = {
                                        if (uiState.inputText.isNotEmpty()) {
                                            IconButton(onClick = { onInputTextChange("") }) {
                                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        capitalization = KeyboardCapitalization.Words
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { onAddEndTrigger() }
                                    )
                                )
                            }
                            uiState.endTriggers.forEach { trigger ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(trigger.name)
                                    Spacer(Modifier.weight(1f))
                                    IconButton(onClick = { onRemoveEndTrigger(trigger) }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                    Switch(
                                        checked = trigger.isOn,
                                        onCheckedChange = { checked -> onToggleEndTrigger(trigger, checked) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Column(modifier = Modifier.padding(4.dp)) {
                Text(
                    "Начало записи",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (showSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showSheet = false },
                        sheetState = sheetState
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 42.dp)
                        ) {
                            Text("Выберите тип триггера", style = MaterialTheme.typography.titleLarge)

                            uiState.startTriggersOptions.forEach { option ->
                                ListItem(
                                    headlineContent = {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(45.dp)
                                                .background(Color.Black.copy(alpha = 0.05f), MaterialTheme.shapes.medium)
                                                .clickable {
                                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                                        if (!sheetState.isVisible) {
                                                            showSheet = false
                                                            onAddStartTrigger(option)
                                                        }
                                                    }
                                                }
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(option)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .background(Color(0xFFF0F0F0), MaterialTheme.shapes.medium)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Триггеры", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { showSheet = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Text("Добавить")
                        }
                    }
                    uiState.startTriggers.forEach { trigger ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(trigger.name, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { onRemoveStartTrigger(trigger) }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                            Switch(
                                checked = trigger.isOn,
                                onCheckedChange = { checked -> onToggleStartTrigger(trigger, checked) }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onApplyChanges,
            enabled = uiState.configIsChanged,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.7f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "Применить изменения")
        }
    }
}