package com.mvi.core.datastore

import com.mvi.core.kotlin.ValueObjectFactory

/**
 * An inline value class representing a validated datastore key identifier.
 *
 * @param DatastoreKey encapsulates a string key that must conform to a strict
 *                      naming convention. It acts as a wrapper around [String] to provide
 *                      type safety and validation at compile-time or runtime, depending
 *                      on usage.
 *
 * @property value The underlying string value of the datastore key.
 *
 * This key is typically used to identify records within a cloud or local datastore
 * (e.g., Cloud Firestore, RSQLite, or custom NoSQL stores) where keys follow a
 * specific hierarchical or named convention.
 *
 * @constructor The internal constructor validates the input string against a
 *               predefined pattern during object creation.
 *
 * @see DatastoreKeyException
 * @see ValueObjectFactory
 */
@JvmInline
value class DatastoreKey internal constructor(val value: String) {

    /**
     * Factory object providing validation and creation utilities for [DatastoreKey].
     *
     * This companion object implements [ValueObjectFactory] to ensure that all
     * [DatastoreKey] instances conform to the expected format before use.
     * It centralizes validation logic, regex definitions, and error handling
     * to prevent the creation of invalid keys.
     *
     * @property pattern A regular expression that defines the valid structure of a
     *                    [DatastoreKey]. The key must start with at least three alphanumeric
     *                    characters or underscores, followed by a colon, then at least
     *                    three alphanumeric characters or underscores.
     *                    Format example: `collection:id` or `item:123`.
     * @property initializer A factory function that creates a new [DatastoreKey]
     *                        instance given a valid string.
     * @property isValid Checks whether a given string conforms to the valid
     *                    [DatastoreKey] pattern.
     * @property getThrowable Returns a [DatastoreKeyException] if the input string
     *                         fails validation checks.
     *
     * @see DatastoreKeyException.InvalidKey
     */
    companion object : ValueObjectFactory<String, DatastoreKey, DatastoreKeyException>() {

        // Regex pattern: ^[a-zA-Z0-9_]{3,}:[a-zA-Z0-9_]{3,}$
        private val pattern = Regex("^[a-zA-Z0-9_]{3,}:[a-zA-Z0-9_]{3,}$")

        override val initializer: (String) -> DatastoreKey = ::DatastoreKey

        override fun isValid(input: String): Boolean {
            return input.isNotEmpty() && pattern.matches(input)
        }

        override fun getThrowable(input: String): DatastoreKeyException? {
            return if (!isValid(input)) DatastoreKeyException.InvalidKey(input) else null
        }

        /**
         * Constructs a fully qualified [DatastoreKey] by combining a namespaced name
         * and a specific key into a single validated key.
         *
         * This internal function is typically used within the datastore API to simplify
         * key composition by concatenating a [DatastoreName] prefix with a user-provided
         * [key] string. It delegates to an internal [get] method to handle the actual
         * validation and construction of the [DatastoreKey].
         *
         * The key format follows the pattern: `{namespace}.preferences_pb:subkey`
         * where the namespace comes from [DatastoreName] and `subkey` is the user-provided key.
         *
         * ## Key Composition Pattern
         *
         * The function combines the `DatastoreName` (e.g., `user_config.preferences_pb`)
         * with the `key` (e.g., `settings`) to produce: `user_config:settings`
         *
         * This ensures that all keys are namespaced correctly and avoids key collisions
         * between different datastore names or environments.
         *
         * @param from The [DatastoreName] prefix that defines the namespaced namespace.
         *             This typically represents a configuration or schema file name
         *             (e.g., `my_config.preferences_pb`, `user_prefs.preferences_pb`).
         * @param key The specific sub-key within the namespaced datastore.
         *            This represents the identifier for the specific value or record.
         *            The combined key will follow the validation pattern of [DatastoreKey].
         * @return A [Result] containing the constructed [DatastoreKey] on success.
         *         On failure (e.g., if the concatenated key is invalid), returns an error
         *         result with a descriptive message (e.g., "Key must match pattern").
         * @see DatastoreName
         * @see DatastoreKey
         * @see KeyedValueDatastore
         */
        fun datastoreKey(from: DatastoreName, key: String): Result<DatastoreKey> = get("${from.name}:$key")
    }
}

/**
 * Exception types associated with [DatastoreKey] violations.
 *
 * This sealed class represents errors that occur when a key is invalid, malformed,
 * or does not conform to the required [DatastoreKey] pattern. It extends
 * [IllegalArgumentException] to signal that an invalid key was used or provided.
 *
 * Subclasses of this exception provide specific context about the failure.
 *
 */
sealed class DatastoreKeyException : IllegalArgumentException() {

    /**
     * Exception indicating that a provided string did not pass validation
     * for the [DatastoreKey].
     *
     * @property message A descriptive message containing the invalid input
     *                    string that caused the validation failure.
     * @see DatastoreKeyException
     */
    data class InvalidKey(override val message: String?) : DatastoreKeyException()
}
