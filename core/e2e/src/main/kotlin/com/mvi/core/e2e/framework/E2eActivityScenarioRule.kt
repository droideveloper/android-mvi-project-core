package com.mvi.core.e2e.framework

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.test.core.app.ActivityScenario
import org.junit.rules.ExternalResource

/**
 * A JUnit rule designed to manage the lifecycle of an [ActivityScenario] specifically for
 * End-to-End (E2E) testing.
 *
 * This rule simplifies testing by automatically handling the launching and cleanup of
 * activities started via a specific [Intent]. It ensures that the activity is
 * properly closed after each test execution, preventing resource leaks.
 *
 * @param T The type of [ComponentActivity] being launched.
 * @property intent The [Intent] used to start the activity, allowing for testing
 * different entry points or deep links.
 * @property scenario The active [ActivityScenario] instance. This is populated upon
 * calling [launch] and cleared after [tearDown].
 */
class E2eActivityScenarioRule<A : ComponentActivity> internal constructor(
    private val intent: Intent,
) : ExternalResource() {

    /**
     * The underlying activity scenario. Note that this property is updated
     * internally by the [launch] and [tearDown] methods.
     */
    var scenario: ActivityScenario<A>? = null

    /**
     * Launches the activity using the provided [Intent].
     *
     * This method initializes the internal [scenario] property and returns the
     * active scenario for further assertions or interactions.
     * @return The launched [ActivityScenario].
     */
    fun launch(): ActivityScenario<A> =
        ActivityScenario.launch<A>(intent).also {
            scenario = it
        }

    /**
     * Manually closes the current [ActivityScenario] and clears it from memory.
     */
    fun tearDown() {
        scenario?.close()
        scenario = null
    }

    /**
     * Overrides the standard lifecycle to ensure that [tearDown] is called
     * automatically after every test case, even if the test fails.
     */
    override fun after() {
        super.after()
        tearDown()
    }
}

/**
 * Factory function to create an [E2eActivityScenarioRule].
 *
 * Use this method to instantiate a rule for a specific activity and intent
 * when defining test classes.
 *
 * @param intent The [Intent] required to start the target activity.
 * @return A configured instance of [E2eActivityScenarioRule].
 */
fun <A : ComponentActivity> createE2eActivityScenarioRule(
    intent: Intent,
) = E2eActivityScenarioRule<A>(intent = intent)
