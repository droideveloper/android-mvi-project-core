package com.mvi.core.mvi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvi.core.coroutines.throttle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class MviViewModel<Event, State>(
    initialState: State,
) : ViewModel() {

    protected val events = MutableSharedFlow<Event>()

    var state by mutableStateOf(initialState)
        protected set

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
