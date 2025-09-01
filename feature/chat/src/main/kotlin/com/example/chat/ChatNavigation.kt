package com.example.chat

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable data object ChatRoute

fun NavGraphBuilder.chatScreen(
    onTopicClick: (String) -> Unit
) {
    composable(ChatRoute.toString()) {
        ChatScreen(onTopicClick)
    }
}
