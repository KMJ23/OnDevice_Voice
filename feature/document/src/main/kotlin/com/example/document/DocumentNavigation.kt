package com.example.document

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable data object DocumentRoute

fun NavGraphBuilder.documentScreen(
    onTopicClick: (String) -> Unit
) {
    composable(DocumentRoute.toString()) {
        DocumentScreen(onTopicClick)
    }
}
