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

/**
 * Abstract base [ViewModel] implementation for MVI architecture using Flow.
 *
 * Provides a reactive state management solution for Compose applications
 * where UI updates are driven by event streams. Extends [ViewModel] to
 * add MVI-specific functionality including event dispatching and handler wiring.
 *
 * @param <E> The event type emitted by this view model
 * @param <S> The state type managed by this view model
 *
 * @property initialState The initial state value used when creating the view model
 */
abstract class FlowMviViewModel<Event, State>(
    initialState: State,
) : ViewModel() {

    /**
     * A [MutableSharedFlow] that emits events to trigger state updates.
     *
     * Use [dispatch] to emit events, or call the [on] and [onClick] helpers
     * to wire event handlers with proper error handling.
     */
    val events = MutableSharedFlow<Event>()

    /**
     * An internal mutable [StateFlow] holding the current state.
     *
     * This property is intentionally protected to prevent direct mutation
     * from outside the class. State changes should be handled via event handlers
     * wired with [on] or [onClick].
     */
    protected val internalState = MutableStateFlow(initialState)

    /**
     * A [StateFlow] property exposing the current state.
     *
     * This is a read-only accessor for [internalState]. Subscribers to this flow
     * will receive the latest state value whenever [internalState] changes.
     */
    val state: StateFlow<State> get() = internalState

    /**
     * Dispatches an event to trigger state handling logic.
     *
     * Emits the provided [event] to the [events] flow, which will be processed
     * by any handlers wired via [on] or [onClick] extensions.
     *
     * @param event The event to dispatch
     */
    fun dispatch(event: Event) = viewModelScope.launch {
        events.emit(event)
    }

    /**
     * Helper function to wire exception-handling event listeners for reified event types.
     *
     * Filters events of the specified type [E], safely executes the provided [block],
     * extracts any exceptions thrown, and handles them via the [onError] callback.
     *
     * This method does not throttle events; it is intended for events that may fire
     * at any time without rate limiting concerns.
     *
     * @param <E> The reified event type to listen for
     * @param block A lambda that performs the desired action when an event of type E is emitted.
     *              This lambda may throw exceptions, which will be caught and handled.
     *
     * @see FlowMviViewModel.onClick
     */
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

    /**
     * Helper function to wire throttled exception-handling event listeners for reified event types.
     *
     * Filters events of the specified type [E], throttles them with [Config.defaultDelay],
     * safely executes the provided [handle] lambda, extracts any exceptions, and handles them
     * via the [onError] callback.
     *
     * Throttling prevents rapid-fire event handling (e.g., multiple clicks in quick succession),
     * ensuring only the last action is processed. This is ideal for button clicks and similar
     * user interactions.
     *
     * @param <E> The reified event type to listen for
     * @param handle A lambda that processes the event. This lambda may throw exceptions,
     *               which will be caught and handled by [onError].
     */
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

    /**
     * Default implementation of the error handler callback.
     *
     * Override this method to define custom error handling logic. By default,
     * exceptions are silently swallowed. Common patterns for override:
     *
     * - Log errors: `{ println(it.message) }`
     * - Show snackbar: `{ showSnackbar("Error: ${it.message}") }`
     * - Navigate to error screen: `{ navigateToErrorScreen(it) }`
     *
     * @see onError
     */
    open fun onError(): Throwable.() -> Unit = { }
}
