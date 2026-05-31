package com.mvi.core.datastore

import com.mvi.core.kotlin.ValueObjectFactory

/**
 * An inline value class representing a validated datastore name identifier.
 *
 * This class encapsulates a string that must conform to a strict naming convention,
 * typically used for identifying configuration, settings, or schema files in a
 * datastore environment (e.g., preferences files, metadata blobs).
 *
 * @param DatastoreName encapsulates a string that represents a valid datastore name.
 *                      Unlike regular [String], this type provides compile-time
 *                      type safety and enforces validation rules for storage names.
 *
 * @property value The underlying string value of the datastore name.
 *
 * This name is typically used in conjunction with [DatastoreKey] to represent
 * fully qualified identifiers for configuration files, schemas, or metadata
 * that follow a hierarchical or namespaced convention.
 *
 * @constructor The internal constructor validates the input string against a
 *               predefined pattern during object creation to ensure only valid
 *               datastore names are instantiated.
 *
 * @see DatastoreNameException
 * @see ValueObjectFactory
 */
@JvmInline
value class DatastoreName internal constructor(val value: String) {

    /**
     * A derived string property that returns the name portion of the datastore name,
     * excluding the `.preferences_pb` suffix.
     *
     * This property provides a cleaner representation of the datastore name for
     * logging, display, or external identification purposes. It is particularly useful
     * when working with configuration files or metadata blobs where the full name
     * includes a standard suffix that needs to be omitted for readability.
     *
     * @return The name portion of the datastore name with the `.preferences_pb` suffix
     *          removed. For example, if the stored value is `user_config.preferences_pb`,
     *          this property returns `user_config`.
     *
     * The transformation is performed by replacing the `.preferences_pb` suffix
     * with an empty string. If the value does not contain this suffix, it is returned
     * unchanged.
     *
     * @property value The underlying string value from which this name is derived.
     * @see DatastoreName
     */
    val name: String get() = value.replace(".preferences_pb", "")


    /**
     * Factory object providing validation and creation utilities for [DatastoreName].
     *
     * This companion object implements [ValueObjectFactory] to ensure that all
     * [DatastoreName] instances conform to the expected format before use.
     * It centralizes validation logic, regex definitions, and error handling
     * to prevent the creation of invalid names.
     *
     * @property pattern A regular expression that defines the valid structure of a
     *                    [DatastoreName]. The name must start with at least five
     *                    alphanumeric characters or underscores, followed by a dot
     *                    and the literal string `.preferences_pb`.
     *                    Format example: `my_settings.preferences_pb` or `config_v1.preferences_pb`.
     *
     * @property initializer A factory function that creates a new [DatastoreName]
     *                        instance given a valid string.
     *
     * @property isValid Checks whether a given string conforms to the valid
     *                    [DatastoreName] pattern.
     *
     * @property getThrowable Returns a [DatastoreNameException] if the input string
     *                         fails validation checks.
     *
     * @see DatastoreNameException.InvalidName
     */
    companion object : ValueObjectFactory<String, DatastoreName, DatastoreNameException>() {

        /**
         * The default datastore name used for application-level default data storage.
         *
         * This constant represents the canonical name for the default datastore used
         * in application initialization, configuration management, and default settings.
         * It is typically referenced when no explicit datastore is configured by the caller.
         *
         * The name follows the [DatastoreName] pattern with a standardized prefix
         * (`default_datastore`) and the `.preferences_pb` suffix.
         *
         * ## Usage Pattern
         *
         * This constant is designed to be imported at the package level and used directly
         * in configuration or initialization code. It ensures consistency across the
         * application by using a single, canonical default datastore name.
         *
         * @property value The underlying string value of the default datastore name
         *                  (`default_datastore.preferences_pb`).
         * @see DatastoreName
         * @see DatastoreKey
         * @see KeyedValueDatastore
         */
        val Default: DatastoreName = DatastoreName("default_datastore.preferences_pb")


        // Regex pattern: ^[a-zA-Z0-9_]{5,}+\.preferences_pb$
        private val pattern = Regex("^[a-zA-Z0-9_]{5,}+\\.preferences_pb$")

        override val initializer: (String) -> DatastoreName = ::DatastoreName

        override fun isValid(input: String): Boolean {
            return input.isNotEmpty() && pattern.matches(input)
        }

        override fun getThrowable(input: String): DatastoreNameException? {
            return if (isValid(input).not()) DatastoreNameException.InvalidName(input) else null
        }
    }
}

/**
 * Exception types associated with [DatastoreName] violations.
 *
 * This sealed class represents errors that occur when a name is invalid, malformed,
 * or does not conform to the required [DatastoreName] pattern. It extends
 * [IllegalArgumentException] to signal that an invalid name was used or provided.
 *
 * Subclasses of this exception provide specific context about the failure,
 * which is particularly useful when deserializing configuration or schema data
 * from external sources.
 *
 */
sealed class DatastoreNameException : IllegalArgumentException() {

    /**
     * Exception indicating that a provided string did not pass validation
     * for the [DatastoreName].
     *
     * This exception is thrown when the input string fails the pattern matching
     * requirements, such as having too few characters before the `.preferences_pb`
     * suffix or missing the required suffix entirely.
     *
     * @property message A descriptive message containing the invalid input
     *                    string that caused the validation failure. The message
     *                    includes the original input value for debugging purposes.
     *
     * @see DatastoreNameException
     * @see DatastoreName
     */
    data class InvalidName(override val message: String?) : DatastoreNameException()
}
