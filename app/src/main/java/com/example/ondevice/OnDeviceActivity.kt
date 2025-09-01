package com.example.ondevice

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ondevice.ui.theme.OnDeviceTheme
import com.example.ui.AppProgressDialog
import com.example.ui.hideProgressDialog
import com.example.ui.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnDeviceActivity : ComponentActivity() {
    @SuppressLint("DesignSystem")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnDeviceTheme {
                CompositionLocalProvider(
                    LocalLifecycleOwner provides this
                ) {
                    TestScreen()
                }
            }
        }
    }
}


// TODO: 임시 테스트 화면
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    onDeviceViewModel: OnDeviceViewModel = hiltViewModel()
) {
    val textFieldFocusRequester = remember { FocusRequester() }
    val buttonFocusRequester = remember { FocusRequester() }
    val defaultFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        defaultFocusRequester.requestFocus()
    }

    val state = onDeviceViewModel.state.collectAsStateWithLifecycle()
    if (state.value.isLoading) {
        showProgressDialog()
    } else {
        hideProgressDialog()
    }

    val filePathsToUpdate = listOf("data/local/tmp/file/tvChannels.xlsx")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chat",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(
                        modifier = Modifier
                            .focusProperties { down = textFieldFocusRequester }
                            .focusRequester(defaultFocusRequester)
                            .focusable(),
                        onClick = { onDeviceViewModel.update(filePathsToUpdate) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Update Documents"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AppProgressDialog()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column {
                QALayout(
                    onDeviceState = state.value
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                QueryInput(
                    onDeviceState = state.value,
                    onDeviceViewModel = onDeviceViewModel,
                    textFieldFocusRequester = textFieldFocusRequester,
                    buttonFocusRequester = buttonFocusRequester
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.QALayout(
    onDeviceState: OnDeviceState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
    ) {
        if (onDeviceState.isUpdate) {
            if (onDeviceState.isSearch) {
                LazyColumn {
                    item {
                        Text(
                            text = onDeviceState.question,
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(24.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = onDeviceState.answer,
                                style =
                                TextStyle(
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                ),
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(75.dp),
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = Color.LightGray,
                    )
                    Text(
                        text = "Success Update Document",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(75.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.LightGray,
                )
                Text(
                    text = "Please Update Document",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                )
            }
        }
    }
}

@Composable
private fun QueryInput(
    onDeviceState: OnDeviceState,
    onDeviceViewModel: OnDeviceViewModel,
    textFieldFocusRequester: FocusRequester,
    buttonFocusRequester: FocusRequester
) {
    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .focusProperties { right = buttonFocusRequester }
                .focusRequester(textFieldFocusRequester)
                .focusable(),
            value = inputText,
            onValueChange = {
                inputText = it
            },
            enabled = onDeviceState.isUpdate,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                disabledTextColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            placeholder = {
                Text(text = "Ask documents...")
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier
                .background(Color.Blue, CircleShape)
                .focusProperties { left = textFieldFocusRequester }
                .focusRequester(buttonFocusRequester)
                .focusable(),
            enabled = onDeviceState.isUpdate,
            onClick = {
                keyboardController?.hide()
                if (inputText.isNotBlank()) {
                    onDeviceViewModel.search(inputText)
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Send query",
                tint = Color.White
            )
        }
    }
}


// TODO: 여러 화면 구현 시 feature 모듈 분리
/*@Composable
fun OnDeviceScreen() {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = DocumentRoute.toString() // TODO: 임시 (RAG 테스트)
    ) {
        chatScreen {
            // TODO: top screen click
        }

        documentScreen {
            // TODO: top screen click
        }
    }
}*/
