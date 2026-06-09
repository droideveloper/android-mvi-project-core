package com.mvi.core.e2e

/**
 * Defines the contract for various states a user can inhabit within the application.
 *
 * Implementations of [UserState] are typically used in state machines or navigation
 * flows to trigger specific initialization logic when a transition occurs.
 */
interface UserState {

    /**
     * Executes the setup logic required for this specific state.
     *
     * This method should be invoked immediately upon entering the state to initialize
     * internal components, prepare data models, or configure UI elements.
     */
    fun setup()

    companion object {

        /**
         * A "Null Object" implementation of [UserState].
         *
         * Use this instance when a specific state is identified by the system but
         * requires no specialized logic or side effects during its setup phase.
         * It serves as a safe default to avoid null checks in conditional logic.
         */
        val Nothing = object : UserState {

            override fun setup() {
                // no-op
            }
        }
    }
}
