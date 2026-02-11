package com.example.assistant_startup.ui.features

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assistant_startup.ui.features.chat.ChatScreen
import com.example.assistant_startup.ui.features.chat.ChatsScreen
import com.example.assistant_startup.ui.features.main.HomeScreen
import com.example.assistant_startup.ui.features.setting.SettingsScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val navBackStackEntryState = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntryState.value?.destination?.route
    Scaffold(
        containerColor = Color(0xFFFDF8F2) //  last one: 0xFFF2EBE2
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(navController)
                }
                composable("chats") {
                    ChatsScreen(navController, showFavorites = false)
                }
                composable ("favorites") {
                    ChatsScreen(navController, showFavorites = true)
                }
                composable("serviceSettings") {
                    SettingsScreen(navController)
                }
                composable("chat/{chatId}") { backStackEntry ->
                    val chatId = backStackEntry.arguments?.getString("chatId")
                    ChatScreen(navController, chatId)
                }
                composable("settings") {

                }
            }
        }
    }
}