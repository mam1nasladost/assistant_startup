package com.example.assistant_startup.ui.features.main

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.elevatedButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import android.Manifest
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import com.example.assistant_startup.MyForegroundService
import com.example.assistant_startup.ui.features.chats.dummyData
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: AssistantViewModel = koinViewModel()) {
    val context = LocalContext.current

    val permissionsToRequest = mutableListOf<String>(
        Manifest.permission.RECORD_AUDIO
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val permissionsState = rememberMultiplePermissionsState(permissionsToRequest)
    val lastChat = dummyData.lastOrNull()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            if (!permissionsState.allPermissionsGranted) {
                AlertDialog(
                    onDismissRequest = {},
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    title = {
                        Text(text = "Требуются разрешения")
                    },
                    text = {
                        Text(
                            text = "Для работы фонового ассистента нужен доступ к микрофону и уведомлениям.",
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        ) {
                            Text("Дать разрешения")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {}) {
                            Text("Позже")
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            ElevatedButton(
                onClick = {
                    navController.navigate("serviceSettings") {
                        {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                shape = CircleShape,
                elevation = elevatedButtonElevation(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(28.dp)
                )
            }

            Box(modifier = Modifier.align(Alignment.Center)) {
//                val aiResponse by viewModel.assistantText.collectAsState()
//                    // Область с текстом
//                    AssistantMessage(text = aiResponse)
//
//                    // Кнопка для теста (потом заменишь на TextField)
//                    Button(onClick = { viewModel.onSendMessage("Расскажи шутку про программистов") }) {
//                        Text("Спросить")
//                    }
                CentralActionButton(
                    text = if (!MyForegroundService.isRunning) "активировать Ассистента" else "деактивировать Ассистента",
                    onClick = {
                        val intent = Intent(context, MyForegroundService::class.java)
                        if (!MyForegroundService.isRunning) {
                            ContextCompat.startForegroundService(context, intent)
                        }
                        else {
                            context.stopService(intent)
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Быстрые действия",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionButton(
                        icon = Icons.Default.History,
                        text = "История\nчатов",
                        onClick = {
                            navController.navigate("chats") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    QuickActionButton(
                        icon = Icons.Default.ChatBubbleOutline,
                        text = "Последний\nчат",
                        onClick = {
                            lastChat?.let {
                                navController.navigate("chat/${it.title}") {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                    QuickActionButton(
                        icon = Icons.Default.FavoriteBorder,
                        text = "Избранное",
                        onClick = {
                            navController.navigate("favorites") {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}