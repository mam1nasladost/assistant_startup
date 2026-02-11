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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.assistant_startup.ui.theme.Assistant_StartupTheme
import kotlinx.coroutines.launch

data class Trigger(
    val name: String,
    val isOn: Boolean
)

@Composable
fun SettingsScreen(navController: NavController) {
    SettingsContent(onBackClick = { navController.navigate("home") }, onConfirm = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(onBackClick: () -> Unit, onConfirm: () -> Unit) {
    var isAutoOff by remember { mutableStateOf(true) }
    var pauseLength by remember { mutableFloatStateOf(5f) }
    var inputText by remember { mutableStateOf("") }
    val endTriggers =
        remember { mutableStateListOf(Trigger("Стоп", true), Trigger("Закончил", false)) }
    val startTriggers =
        remember { mutableStateListOf(Trigger("Риски", true), Trigger("Предложения", false)) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val startTriggersOptions = listOf("Риски", "Лимиты", "События", "Цена")
    var configIsChanged by remember { mutableStateOf(true) }

    Box() {
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
                    Switch(isAutoOff, onCheckedChange = { isAutoOff = it; configIsChanged = false })
                }

                Column {
                    AnimatedVisibility(
                        visible = isAutoOff,
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
                                "${pauseLength.toInt()} секунд",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Slider(
                                value = pauseLength,
                                onValueChange = { pauseLength = it; configIsChanged = true },
                                valueRange = 0f..10f,
                                steps = 9
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = !isAutoOff,
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
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    label = { Text("Добавить триггер") },
                                    placeholder = { Text("Введите текст...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    leadingIcon = {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                    },
                                    trailingIcon = {
                                        if (inputText.isNotEmpty()) {
                                            IconButton(onClick = { inputText = "" }) {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Clear"
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        capitalization = KeyboardCapitalization.Words
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (inputText.isNotBlank() && !(endTriggers.contains(
                                                    Trigger(name = inputText, isOn = true)
                                                ) || endTriggers.contains(
                                                    Trigger(
                                                        name = inputText,
                                                        isOn = false
                                                    )
                                                ))
                                            ) {
                                                endTriggers.add(Trigger(inputText.trim(), true))
                                                inputText = ""
                                                configIsChanged = true
                                            }
                                        }
                                    )
                                )
                            }
                            endTriggers.forEachIndexed { index, trigger ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(trigger.name)
                                    Spacer(Modifier.weight(1f))
                                    IconButton(onClick = { endTriggers.removeAt(index) }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                    Switch(
                                        checked = trigger.isOn,
                                        onCheckedChange = { checked ->
                                            endTriggers[index] = trigger.copy(isOn = checked)
                                            configIsChanged = true
                                        }
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
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    if (showSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showSheet = false },
                            sheetState = sheetState
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Выберите тип триггера",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                startTriggersOptions.forEach { option ->
                                    ListItem(
                                        headlineContent = {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(45.dp)
                                                    .background(
                                                        Color.Black.copy(alpha = 0.05f),
                                                        MaterialTheme.shapes.medium
                                                    )
                                                    .clickable {
                                                        scope.launch { sheetState.hide() }
                                                            .invokeOnCompletion {
                                                                if (!sheetState.isVisible) {
                                                                    showSheet = false
                                                                    if (!(startTriggers.contains(
                                                                            Trigger(
                                                                                name = option,
                                                                                isOn = true
                                                                            )
                                                                        ) || startTriggers.contains(
                                                                            Trigger(
                                                                                name = option,
                                                                                isOn = false
                                                                            )
                                                                        ))
                                                                    ) {
                                                                        startTriggers.add(
                                                                            Trigger(
                                                                                option.trim(),
                                                                                true
                                                                            )
                                                                        )
                                                                        configIsChanged = true
                                                                    }
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
                }

                Column(
                    modifier = Modifier
                        .background(
                            Color(0xFFF0F0F0),
                            MaterialTheme.shapes.medium
                        )
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
                    startTriggers.forEachIndexed { index, trigger ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(trigger.name, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { startTriggers.removeAt(index) }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                            Switch(
                                checked = trigger.isOn,
                                onCheckedChange = { checked ->
                                    startTriggers[index] = trigger.copy(isOn = checked)
                                    configIsChanged = true
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onConfirm(); configIsChanged = false },
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsPreview() {
    Assistant_StartupTheme {
        SettingsContent(onBackClick = {}, onConfirm = {})
    }
}