package com.mvi.core.ui.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * A [Composable] that registers a callback to be executed when the screen enters the CREATE lifecycle state.
 *
 * This is typically the first lifecycle event after a Composable is first displayed,
 * often used for one-time initialization that should not persist beyond creation.
 *
 * @param onCreate The callback to execute when the screen is created.
 *
 * ## Example
 * ```kotlin
 * OnScreenCreate {
 *     dispatch(Event.OnCreate)
 * }
 * ```
 *
 * @see OnScreenStart
 * @see OnScreenResume
 */
@Composable
fun OnScreenCreate(
    onCreate: () -> Unit,
) {
    OnLifecycleEffect(onCreate = onCreate)
}

/**
 * A [Composable] that registers a callback to be executed when the screen enters the START lifecycle state.
 *
 * The START state is entered when the screen becomes visible to the user. This lifecycle event
 * fires after the screen has been resumed and is ready to interact with the user.
 *
 * @param onStart The callback to execute when the screen starts.
 *
 * ## Example
 * ```kotlin
 * OnScreenStart {
 *     dispatch(Event.OnStart)
 * }
 * ```
 *
 * @see OnScreenCreate
 * @see OnScreenResume
 */
@Composable
fun OnScreenStart(
    onStart: () -> Unit,
) {
    OnLifecycleEffect(onStart = onStart)
}

/**
 * A [Composable] that registers a callback to be executed when the screen enters the STOP lifecycle state.
 *
 * The STOP state is entered when the screen is no longer visible, typically when another screen
 * is shown on top. Use this for cleanup operations that should occur before the screen is
 * fully removed from memory.
 *
 * @param onStop The callback to execute when the screen stops.
 *
 * ## Example
 * ```kotlin
 * OnScreenStop {
 *     dispatch(Event.OnStop)
 * }
 * ```
 *
 * @see OnScreenStart
 * @see OnScreenPause
 */
@Composable
fun OnScreenStop(
    onStop: () -> Unit,
) {
    OnLifecycleEffect(onStop = onStop)
}

/**
 * A [Composable] that registers a callback to be executed when the screen enters the RESUME lifecycle state.
 *
 * The RESUME state is entered when the screen becomes visible again after being paused or stopped.
 * This lifecycle event fires when the user navigates back to this screen and it becomes active.
 *
 * @param onResume The callback to execute when the screen resumes.
 *
 * ## Example
 * ```kotlin
 * OnScreenResume {
 *     dispatch(Event.OnResume)
 * }
 * ```
 *
 * @see OnScreenStart
 * @see OnScreenPause
 */
@Composable
fun OnScreenResume(
    onResume: () -> Unit,
) {
    OnLifecycleEffect(onResume = onResume)
}

/**
 * A [Composable] that registers a callback to be executed when the screen enters the PAUSE lifecycle state.
 *
 * The PAUSE state is entered when the screen is no longer the foreground screen, typically when
 * the user navigates to another screen. Use this for cleanup operations that should occur before
 * the screen is stopped.
 *
 * @param onPause The callback to execute when the screen pauses.
 *
 * ## Example
 * ```kotlin
 * OnScreenPause {
 *     dispatch(Event.OnPause)
 * }
 * ```
 *
 * @see OnScreenStart
 * @see OnScreenResume
 */
@Composable
fun OnScreenPause(
    onPause: () -> Unit,
) {
    OnLifecycleEffect(onPause = onPause)
}

@Composable
internal fun OnLifecycleEffect(
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = remember { lifecycleOwner.lifecycle }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> onCreate()
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_RESUME -> onResume()
                Lifecycle.Event.ON_PAUSE -> onPause()
                Lifecycle.Event.ON_STOP -> onStop()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
