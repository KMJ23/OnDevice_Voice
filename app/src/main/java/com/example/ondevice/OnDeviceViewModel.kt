package com.example.ondevice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rag.RagAgent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject


internal const val TAG = "ondevice_app"

@HiltViewModel
class OnDeviceViewModel @Inject constructor(
    private val ragAgent: RagAgent
): ViewModel() {

    private val events = Channel<OnDeviceEvent>()
    val state = events.receiveAsFlow()
        .runningFold(OnDeviceState(), ::updateEvents)
        .stateIn(viewModelScope, SharingStarted.Eagerly, OnDeviceState())

    private suspend fun updateEvents(
        current: OnDeviceState,
        event: OnDeviceEvent
    ): OnDeviceState {

        return when (event) {
            is OnDeviceEvent.Loading -> {
                current.copy(
                    isLoading = true
                )
            }

            is OnDeviceEvent.Update -> {
                current.copy(
                    isLoading = false,
                    isUpdate = true,
                    filePaths = event.filePaths
                )
            }

            is OnDeviceEvent.Search -> {
                val resultMap = ragAgent.runRAGPipeline(current.filePaths, event.message)
                val answer = resultMap["response"] as? String ?: "No response"

                current.copy(
                    isLoading = false,
                    isSearch = true,
                    question = event.message,
                    answer = answer
                )
            }
        }
    }

    private fun sendEvent(
        event: OnDeviceEvent
    ) {
        viewModelScope.launch {
            events.send(event)
        }
    }

    fun update(filePaths: List<String>) {
        if (state.value.isLoading) {
            Log.i(TAG, "state is loading")
        } else {
            sendEvent(OnDeviceEvent.Loading)
            sendEvent(OnDeviceEvent.Update(filePaths))
        }
    }

    fun search(message: String) {
        if (state.value.isLoading) {
            Log.i(TAG, "state is loading")
        } else {
            sendEvent(OnDeviceEvent.Loading)
            sendEvent(OnDeviceEvent.Search(message))
        }
    }
}


sealed class OnDeviceEvent {
    data object Loading: OnDeviceEvent()
    data class Update(val filePaths: List<String>): OnDeviceEvent()
    data class Search(val message: String): OnDeviceEvent()
}

@Immutable
data class OnDeviceState(
    val isLoading: Boolean = false,
    val isUpdate: Boolean = false,
    val isSearch: Boolean = false,
    val question: String = "",
    val answer: String = "",
    val filePaths: List<String> = emptyList()
)