package com.example.ondevice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rag.RagManager
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
    private val ragManager: RagManager
): ViewModel() {

    //private val _sideEffect: Channel<OnDeviceState> = Channel()
    //val sideEffect = _sideEffect.receiveAsFlow()

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
                val result = ragManager.updateExternalData()

                current.copy(
                    isLoading = false,
                    isUpdate = result,
                    isSearch = false,
                    question = ""
                )
            }

            is OnDeviceEvent.Search -> {
                val answer = ragManager.search(event.message)

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

    fun update() {
        if (state.value.isLoading) {
            Log.i(TAG, "state is loading")
        } else {
            sendEvent(OnDeviceEvent.Loading)
            sendEvent(OnDeviceEvent.Update)
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
    data object Update: OnDeviceEvent()
    data class Search(val message: String): OnDeviceEvent()
}

//sealed class OnDeviceSideEffect {
//    data object ShowToast: OnDeviceSideEffect()
//}

@Immutable
data class OnDeviceState(
    val isLoading: Boolean = false,
    val isUpdate: Boolean = false,
    val isSearch: Boolean = false,
    val question: String = "",
    val answer: String = ""
)