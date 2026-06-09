package com.mvi.core.e2e.framework

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule

/**
 * Creates an [AndroidComposeTestRule] that integrates seamlessly with an existing
 * [E2eActivityScenarioRule].
 *
 * This factory method bridges the gap between standard End-to-End (E2E) testing
 * infrastructure and Compose-specific UI testing requirements. It wraps the
 * provided activity rule into a compose-compatible rule by automatically providing
 * the necessary activity provider logic.
 *
 * @param rule The [E2eActivityScenarioRule] used to launch and manage the
 *               target [ComponentActivity].
 * @return A configured [AndroidComposeTestRule] ready for use in Compose UI tests.
 *
 * ## Usage
 * ```kotlin
 * @get:Rule
 * val activityRule = E2eActivityScenarioRule(MainActivity::class.java)
 *
 * @get:Rule
 * val composeRule = createE2eComposeTestRule(activityRule)
 *
 * @Test
 * fun testLoginFlow() {
 *     composeRule.onNodeWithText("Login").performClick()
 * }
 * ```
 */
fun <A : ComponentActivity> createE2eComposeTestRule(
    rule: E2eActivityScenarioRule<A>,
) = AndroidComposeTestRule(
    activityRule = rule,
    activityProvider = ::getActivityFromScenarioRule,
)

/**
 * Internal helper to extract the underlying [ComponentActivity] from an
 * [E2eActivityScenarioRule].
 *
 * This function is used as a provider for the [AndroidComposeTestRule]. It
 * utilizes the `onActivity` callback to safely retrieve the activity instance
 * during the test lifecycle.
 *
 * @param rule The E2E scenario rule containing the activity context.
 * @return The active [ComponentActivity] instance.
 * @throws IllegalArgumentException if the activity cannot be retrieved from the
 * provided scenario (e.g., if the scenario has not been started or is
 * currently in an invalid state).
 */
internal fun <A : ComponentActivity> getActivityFromScenarioRule(
    rule: E2eActivityScenarioRule<A>,
): A {
    var activity: A? = null
    rule.scenario?.onActivity {
        activity = it
    }
    return requireNotNull(activity) {
        "Could not find activity on E2eActivityScenarioRule"
    }
}
