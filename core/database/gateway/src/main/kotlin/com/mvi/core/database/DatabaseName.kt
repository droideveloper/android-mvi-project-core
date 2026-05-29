package com.mvi.core.database

import com.mvi.core.kotlin.ValueObjectFactory

/**
 * A value object representing the logical name of a database file.
 * Encapsulates the database name string with validation logic.
 */
@JvmInline
value class DatabaseName private constructor(val value: String) {

    /**
     * Factory companion object for creating and validating [DatabaseName] instances.
     */
    companion object : ValueObjectFactory<String, DatabaseName, DatabaseNameException>() {
        /**
         * Creates a new [DatabaseName] instance from a validated string.
         */
        override val initializer: (String) -> DatabaseName = ::DatabaseName

        /**
         * Checks whether the provided input string is a valid database name.
         * A valid name is not empty and ends with ".db".
         *
         * @param input The string to validate.
         * @return true if the string is valid, false otherwise.
         */
        override fun isValid(input: String): Boolean {
            return input.isNotEmpty() && input.endsWith(".db")
        }

        /**
         * Returns a validation exception if the input is invalid.
         *
         * @param input The string to validate.
         * @return A [DatabaseNameException] if invalid, null otherwise.
         */
        override fun getThrowable(input: String): DatabaseNameException? {
            return if (isValid(input).not()) DatabaseNameException.InvalidNameException(input) else null
        }
    }
}

/**
 * Exception hierarchy for database name validation errors.
 */
sealed class DatabaseNameException : IllegalArgumentException() {
    /**
     * Thrown when a database name fails validation.
     */
    data class InvalidNameException(override val message: String?) : DatabaseNameException()
}
