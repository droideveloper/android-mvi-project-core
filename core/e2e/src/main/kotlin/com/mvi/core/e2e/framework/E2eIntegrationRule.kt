package com.mvi.core.e2e.framework

import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.test.platform.app.InstrumentationRegistry
import com.mvi.core.e2e.UserState
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A specialized [TestWatcher] designed for End-to-End (E2E) integration tests.
 *
 * This rule automates several critical lifecycle tasks required for reliable E2E testing:
 * 1. **Animation Control**: Automatically disables system animations before a test starts
 *    and re-enables them upon completion to ensure deterministic UI interactions.
 * 2. **Activity Management**: Wraps an [E2eActivityScenarioRule] to manage the
 *    lifecycle of the [ComponentActivity].
 * 3. **State Injection**: Provides a convenient mechanism to transition the application
 *    into specific states (via [UserState]) before launching the activity.
 *
 * @param <A> The type of [ComponentActivity] being tested.
 */
class E2eIntegrationRule<A : ComponentActivity> internal constructor(
    private val rule: E2eActivityScenarioRule<A>,
) : TestWatcher() {

    /**
     * Triggered before each test. Disables system-wide animations to ensure
     * that UI elements are immediately interactable and transitions are not
     * hampered by timing issues.
     */
    override fun starting(description: Description?) {
        setAnimations(enabled = false)
    }

    /**
     * Triggered after each test. Re-enables system animations and tears down
     * the activity scenario to ensure a clean environment for subsequent tests.
     */
    override fun finished(description: Description?) {
        rule.tearDown()
        setAnimations(enabled = true)
    }

    /**
     * Configures the required [UserState] and launches the activity for the
     * current test.
     *
     * This is the preferred method to start a test when specific data or
     * configurations (e.g., "Logged In", "Empty Cart") are required before
     * the UI becomes interactive.
     *
     * @param state The [UserState] configuration to apply before launch.
     */
    fun launchForState(state: UserState) {
        state.setup()
        rule.launch()
    }

    /**
     * Executes shell commands via `UiAutomation` to modify the system's
     * animation scale settings.
     *
     * @param enabled If true, sets scaling to 1 (enabled). If false, sets
     *                 scaling to 0 (disabled).
     */
    private fun setAnimations(enabled: Boolean) {
        val value = if (enabled) 1 else 0
        val uiAnimation = InstrumentationRegistry.getInstrumentation().uiAutomation
        with(uiAnimation) {
            executeShellCommand("settings put global ${Settings.Global.WINDOW_ANIMATION_SCALE} $value")
            executeShellCommand("settings put global ${Settings.Global.TRANSITION_ANIMATION_SCALE} $value")
            executeShellCommand("settings put global ${Settings.Global.ANIMATOR_DURATION_SCALE} $value")
        }
    }
}

/**
 * Factory function to create an [E2eIntegrationRule] for a specific
 * [ComponentActivity].
 *
 * Use this method instead of direct instantiation to maintain a clean public API
 * while keeping the internal implementation details encapsulated.
 *
 * @param rule The base [E2eActivityScenarioRule] provided by the test suite.
 * @return A fully configured [E2eIntegrationRule].
 *
 * ## Usage
 * ```kotlin
 * @get:Rule
 * val integrationRule = createE2eIntegrationRule(E2eActivityScenarioRule(MainActivity::class.java))
 * ```
 */
fun <A : ComponentActivity> createE2eIntegrationRule(
    rule: E2eActivityScenarioRule<A>
) = E2eIntegrationRule(rule)
