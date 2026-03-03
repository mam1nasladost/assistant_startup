package com.example.assistant_startup

import com.example.assistant_startup.domain.repository.AssistantRepo
import com.example.assistant_startup.domain.repository.ChatRepo
import com.example.assistant_startup.domain.repository.ChatsRepo
import com.example.assistant_startup.domain.repository.SettingsRepo
import com.example.assistant_startup.remote.KtorClient
import com.example.assistant_startup.remote.repository_imp.AssistantRepoImp
import com.example.assistant_startup.remote.repository_imp.ChatRepoImp
import com.example.assistant_startup.remote.repository_imp.ChatsRepoImp
import com.example.assistant_startup.remote.repository_imp.SettingsRepoImp
import com.example.assistant_startup.ui.features.chat.ChatViewModel
import com.example.assistant_startup.ui.features.chats.ChatsViewModel
import com.example.assistant_startup.ui.features.main.AssistantViewModel
import com.example.assistant_startup.ui.features.setting.SettingsViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModules = {
    listOf(networkModule, repositoriesModule, viewModelsModule)
}

val networkModule = module {
    single {KtorClient()}
}

val repositoriesModule = module {
    single<AssistantRepo> { AssistantRepoImp(get()) }
    single<ChatRepo> { ChatRepoImp(get()) }
    single<ChatsRepo> { ChatsRepoImp(get()) }
    single<SettingsRepo> { SettingsRepoImp(get()) }
}

val viewModelsModule = module {
    viewModel { ChatsViewModel(get()) }
    viewModel { ChatViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { AssistantViewModel(get()) }
}