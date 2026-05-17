package com.mvi.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvi.core.coroutines.throttle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class FlowMviViewModel<Event, State>(
    initialState: State,
) : ViewModel() {

    val events = MutableSharedFlow<Event>()

    protected val internalState = MutableStateFlow(initialState)
    val state: StateFlow<State> get() = internalState

    fun dispatch(event: Event) = viewModelScope.launch {
        events.emit(event)
    }

    protected inline fun <reified E : Event> on(
        crossinline block: suspend E.() -> Unit,
    ) {
        events
            .filterIsInstance<E>()
            .map { runCatching { block(it) } }
            .map { it.exceptionOrNull() }
            .filterNotNull()
            .onEach(onError())
            .launchIn(viewModelScope)
    }

    protected inline fun <reified E : Event> onClick(
        crossinline handle: suspend (event: E) -> Unit,
    ) {
        events
            .filterIsInstance<E>()
            .throttle(Config.defaultDelay)
            .map { runCatching { handle(it) } }
            .map { it.exceptionOrNull() }
            .filterNotNull()
            .onEach(onError())
            .launchIn(viewModelScope)
    }

    open fun onError(): Throwable.() -> Unit = { }
}
