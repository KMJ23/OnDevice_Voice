package com.example.document

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ondevice.ui.theme.OnDeviceTheme

@Composable
internal fun DocumentScreen(
    onTopicClick: (String) -> Unit
) {

}


@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun DocumentScreenPreview() {
    OnDeviceTheme {
        DocumentScreen {

        }
    }
}
