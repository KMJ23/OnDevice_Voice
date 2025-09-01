package com.example.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ondevice.ui.theme.OnDeviceTheme

@Composable
internal fun ChatScreen(
    onTopicClick: (String) -> Unit
) {

}


@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ChatScreenPreview() {
    OnDeviceTheme {
        ChatScreen {

        }
    }
}
