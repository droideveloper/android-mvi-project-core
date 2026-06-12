@file:OptIn(ExperimentalCoroutinesApi::class)

package com.mvi.core.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

/**
 * Base test class for coroutines testing with unconfined test dispatchers.
 *
 * This class sets up the main dispatcher to use [UnconfinedTestDispatcher]
 * which allows coroutines to be tested without the overhead of dispatchers.
 * All child test classes should extend this class to get automatic dispatcher
 * setup and teardown.
 *
 * ## Usage
 * ```kotlin
 * class MyUsecaseTest : AbsDispatcherTest() {
 *
 *     private val myUsecase = MyUsecase()
 *
 *     @Test
 *     fun `given xxx state than yyy observed/state/failure`() = runTest {
 *         val result = myUsecase()
 *
 *         val actual = result.getOrThrow()
 *         assertThat(actual, expected).isEquals()
 *     }
 * }
 * ```
 *
 * ## Threading
 * Uses [UnconfinedTestDispatcher] which allows suspension points to be
 * evaluated immediately in the current context. This is faster but requires
 * careful testing to ensure proper completion.
 *
 * @see UnconfinedTestDispatcher
 */
abstract class AbsDispatcherTest {

    /**
     * Sets up the main dispatcher to use [UnconfinedTestDispatcher]
     * before each test execution.
     */
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    /**
     * Resets the main dispatcher to its original state after each test.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
