package com.example.assistant_startup.ui.features.main

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.assistant_startup.ui.features.chat.dummyData

@Composable
fun HomeScreen(navController: NavController) {
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
//            IconButton(
//
//                onClick = {
//                    navController.navigate("settings") {
//                        {
//                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
//                            launchSingleTop = true
//                            restoreState = true
//                        }
//                    }
//                },
//                modifier = Modifier
//                    .size(52.dp)
//                    .shadow(1.dp, CircleShape)
//                    .background(Color.White, CircleShape)
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.Settings,
//                    contentDescription = "Settings",
//                    modifier = Modifier.size(28.dp)
//                )
//            }
            ElevatedButton(
                onClick = {
                    navController.navigate("serviceSettings") {
                        {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
                CentralActionButton()
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
//                            lastChat?.let {
//                                navController.navigate("chat/${it.title}") {
//                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
//                                    launchSingleTop = true
//                                    restoreState = true
//                                }
//                            }
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