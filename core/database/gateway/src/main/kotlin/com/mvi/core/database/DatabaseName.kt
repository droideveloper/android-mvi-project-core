package com.mvi.core.database

import com.mvi.core.kotlin.ValueObjectFactory

/**
 * A value object representing the logical name of a database file.
 * Encapsulates the database name string with validation logic.
 */
@JvmInline
value class DatabaseName private constructor(val value: String) {

    /**
     * A derived string property that returns the name portion of the datastore name,
     * excluding the `.db` suffix.
     *
     * This property provides a cleaner representation of the datastore name for
     * logging, display, or external identification purposes. It is particularly useful
     * when working with configuration files or metadata blobs where the full name
     * includes a standard suffix that needs to be omitted for readability.
     *
     * @return The name portion of the database name with the `.db` suffix
     *          removed. For example, if the stored value is `user_database.db`,
     *          this property returns `user_database`.
     *
     * The transformation is performed by replacing the `.db` suffix
     * with an empty string. If the value does not contain this suffix, it is returned
     * unchanged.
     *
     * @property value The underlying string value from which this name is derived.
     * @see DatabaseName
     */
    val name: String get() = value.replace(".db", "")

    /**
     * Factory companion object for creating and validating [DatabaseName] instances.
     */
    companion object : ValueObjectFactory<String, DatabaseName, DatabaseNameException>() {

        /**
         * The default datastore name used for application-level default data storage.
         *
         * This constant represents the canonical name for the default datastore used
         * in application initialization, configuration management, and default settings.
         * It is typically referenced when no explicit datastore is configured by the caller.
         *
         * The name follows the [DatabaseName] pattern with a standardized prefix
         * (`default_database`) and the `.db` suffix.
         *
         * ## Usage Pattern
         *
         * This constant is designed to be imported at the package level and used directly
         * in configuration or initialization code. It ensures consistency across the
         * application by using a single, canonical default datastore name.
         *
         * @property value The underlying string value of the default datastore name
         *                  (`default_database.db`).
         * @see DatabaseName
         */
        val Default: DatabaseName = DatabaseName("default_database.db")

        // Regex pattern: ^[a-zA-Z0-9_]{5,}+\.db
        private val pattern = Regex("^[a-zA-Z0-9_]{5,}+\\.db$")

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
            return input.isNotEmpty() && pattern.matches(input)
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
