package com.mvi.core.e2e.framework

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.core.app.ApplicationProvider
import com.mvi.core.e2e.UserState

/**
 * Concrete implementation of [E2eTest] that initializes and manages the
 * underlying test infrastructure for a given [ComponentActivity].
 *
 * This class serves as the wiring layer, combining Activity lifecycle rules,
 * integration logic (such as mock data or network state), and Compose interaction
 * rules into a single unified testing unit.
 *
 * @param clazz The class type of the activity to be tested.
 */
class E2eTestImpl<A : ComponentActivity>(
    clazz: Class<A>,
) : E2eTest<A> {

    /**
     * Configures the [E2eActivityScenarioRule] specifically for the
     * target [ComponentActivity].
     */
    override val activityScenarioRule: E2eActivityScenarioRule<A> =
        createE2eActivityScenarioRule(
            intent = Intent(
                ApplicationProvider.getApplicationContext(),
                clazz,
            )
        )

    /**
     * Configures the [E2eIntegrationRule], which handles non-UI logic
     * such as database states or API mocking prior to interaction.
     */
    override val integrationRule: E2eIntegrationRule<A> =
        createE2eIntegrationRule(
            rule = activityScenarioRule,
        )

    /**
     * Configures the [AndroidComposeTestRule] for interacting with
     * Compose UI components.
     */
    override val testRule: AndroidComposeTestRule<E2eActivityScenarioRule<A>, A> =
        createE2eComposeTestRule(
            rule = activityScenarioRule,
        )

    /**
     * Executes a block of test actions after preparing the environment
     * with the specified [UserState].
     *
     * This method orchestrates the transition from "state setup" via [integrationRule]
     * to "UI interaction" via [testRule].
     *
     * @param state The required initial state for the test.
     * @param block A lambda containing the test steps, executed within the scope of [testRule].
     */
    override fun launchForUserState(state: UserState, block: ComposeTestRule.() -> Unit) {
        with(testRule) {
            integrationRule.launchForState(state)
            block(this)
        }
    }
}

/**
 * A factory function to create a configured [E2eTest] instance for a specific
 * activity type using reified types.
 *
 * This provides a convenient, type-safe way to initialize E2E tests without
 * manually passing Class references in the test files.
 *
 * @param A The [ComponentActivity] class to be used as the test target.
 * @return A fully configured instance of [E2eTest].
 *
 * ## Usage
 * ```kotlin
 * internal class MyFlowTest : E2eTest by createE2eTest<MainActivity>() {
 * }
 * ```
 */
inline fun <reified A : ComponentActivity> createE2eTest() =
    E2eTestImpl(clazz = A::class.java)
