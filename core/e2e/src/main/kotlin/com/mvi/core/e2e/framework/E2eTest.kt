package com.mvi.core.e2e.framework

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.mvi.core.e2e.UserState
import org.junit.Rule

/**
 * A base contract for End-to-End (E2E) tests involving [ComponentActivity] and Jetpack Compose.
 *
 * This interface abstracts the boilerplate setup required for E2E testing, including
 * activity lifecycle management, integration rules, and Compose test interactions.
 * Implementations of this interface should provide specific configurations for the targeted
 * Activity within a test suite.
 *
 * @param A The type of [ComponentActivity] being tested.
 */
interface E2eTest<A : ComponentActivity> {

    /**
     * Rule to manage the lifecycle of the activity used in the test.
     */
    @get:Rule
    val activityScenarioRule: E2eActivityScenarioRule<A>

    /**
     * Rules for handling integration-specific logic (e.g., MockWebServer,
     * local database states, or network interceptors).
     */
    @get:Rule
    val integrationRule: E2eIntegrationRule<A>

    /**
     * The primary rule for interacting with the Compose UI components.
     */
    @get:Rule
    val testRule: AndroidComposeTestRule<E2eActivityScenarioRule<A>, A>

    /**
     * Launches the application's activity under a specific [UserState] and
     * executes the provided block of test actions.
     *
     * This method is typically used when the initial UI state (e.g., logged in,
     * empty state, or specific feature flag status) needs to be predefined before
     * performing interactions.
     *
     * @param state The initial [UserState] context for the launch.
     * @param block A lambda expression containing the test steps to perform on the [ComposeTestRule].
     */
    fun launchForUserState(state: UserState, block: ComposeTestRule.() -> Unit)
}

/**
 * Shortcut method to launch the activity without a specific initial state.
 *
 * This extension calls [E2eTest.launchForUserState] using [UserState.Nothing].
 * It is intended for standard scenarios where no complex initial configuration
 * is required before interacting with the UI.
 *
 * @param block A lambda expression containing the test steps to perform on the [ComposeTestRule].
 */
fun <A : ComponentActivity> E2eTest<A>.launch(block: ComposeTestRule.() -> Unit) {
    launchForUserState(state = UserState.Nothing, block = block)
}
