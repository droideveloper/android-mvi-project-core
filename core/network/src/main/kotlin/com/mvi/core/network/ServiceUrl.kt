package com.mvi.core.network

import com.mvi.core.kotlin.ValueObjectFactory

/**
 * A value object representing a valid service URL.
 *
 * A [ServiceUrl] wraps a String and validates that it uses the http or https protocol.
 * This type-safe wrapper prevents invalid URLs from propagating through the codebase
 * and provides clear error messages when URL validation fails.
 *
 * ## Usage
 * ```kotlin
 * val url = ServiceUrl("https://api.example.com")
 * val url from string = ServiceUrl.from("https://api.example.com") // valid
 * val url = ServiceUrl.from("ftp://invalid.com") // throws ServiceUrlException.InvalidUrl
 * ```
 *
 * @property value The validated URL string, must start with "http://" or "https://"
 */
@JvmInline
value class ServiceUrl private constructor(
    val value: String
) {
    /**
     * Companion object providing factory methods for creating [ServiceUrl] instances.
     *
     * This companion object implements [ValueObjectFactory] to provide a type-safe,
     * exception-throwing API for URL validation and creation.
     *
     * ## Factory Methods
     *
     * ### [from]
     * Creates a [ServiceUrl] from a String, throwing [ServiceUrlException] on validation failure.
     * ```kotlin
     * val url = ServiceUrl.from("https://api.example.com")
     * ```
     *
     * ### [isValid]
     * Checks whether a given String represents a valid service URL (starts with http:// or https://).
     * ```kotlin
     * if (ServiceUrl.isValid("https://api.example.com")) { ... }
     * ```
     *
     * ### [getThrowable]
     * Returns a [ServiceUrlException] if the input is invalid, or null if valid.
     * This method supports the ValueObjectFactory pattern for optional exception handling.
     * ```kotlin
     * val exception = ServiceUrl.getThrowable("invalid-url")
     * val exception = ServiceUrl.getThrowable("https://valid.com") // null
     * ```
     */
    companion object : ValueObjectFactory<String, ServiceUrl, ServiceUrlException>() {

        /**
         * Creates a [ServiceUrl] from a String.
         *
         * @param input The URL string to validate and wrap
         * @return A [ServiceUrl] wrapping the validated input
         * @throws [ServiceUrlException.InvalidUrl] if the input doesn't start with "http://" or "https://"
         *
         * ## Examples
         * ```kotlin
         * val validUrl = ServiceUrl.from("https://api.example.com") // succeeds
         * val url = ServiceUrl.from("ftp://example.com") // throws ServiceUrlException.InvalidUrl
         * ```
         */
        override val initializer: (String) -> ServiceUrl = ::ServiceUrl

        /**
         * Checks whether a given String represents a valid service URL.
         *
         * A URL is considered valid if it starts with "http://" or "https://".
         *
         * @param input The URL string to check
         * @return `true` if the input starts with "http://" or "https://", `false` otherwise
         *
         * ## Examples
         * ```kotlin
         * ServiceUrl.isValid("https://api.example.com")   // true
         * ServiceUrl.isValid("http://api.example.com")    // true
         * ServiceUrl.isValid("ftp://example.com")          // false
         * ServiceUrl.isValid("example.com")                // false
         * ServiceUrl.isValid("api.example.com")            // false
         * ```
         */
        override fun isValid(input: String): Boolean = when {
            input.startsWith("http://") || input.startsWith("https://") -> true
            else -> false
        }

        /**
         * Returns a [ServiceUrlException] if the input is invalid, or null if valid.
         *
         * This method supports the ValueObjectFactory pattern for optional exception handling.
         * Use this when you want to validate a URL without throwing an exception.
         *
         * @param input The URL string to validate
         * @return `null` if the input is valid, or a [ServiceUrlException.InvalidUrl] if invalid
         *
         * ## Examples
         * ```kotlin
         * val exception = ServiceUrl.getThrowable("invalid-url") // returns ServiceUrlException.InvalidUrl
         * val exception = ServiceUrl.getThrowable("https://valid.com") // returns null
         * ```
         */
        override fun getThrowable(input: String): ServiceUrlException? = when {
            isValid(input) -> null
            else -> ServiceUrlException.InvalidUrl
        }
    }
}

/**
 * Exception thrown when a [ServiceUrl] fails validation.
 *
 * This exception extends [IllegalArgumentException] and is thrown by the
 * [ServiceUrl] companion object factory when an invalid URL is provided.
 *
 * ## When Thrown
 *
 * This exception is thrown in the following scenarios:
 * - When [ServiceUrl.from()] is called with a URL that doesn't start with "http://" or "https://"
 * - When [ServiceUrl.getThrowable()] is called with an invalid URL (returns the exception instance)
 *
 * ## Usage
 * ```kotlin
 * try {
 *     val url = ServiceUrl.from("ftp://invalid.com")
 * } catch (e: ServiceUrlException.InvalidUrl) {
 *     // Handle invalid URL
 * }
 * ```
 *
 * @property message The exception message describing the validation failure
 */
sealed class ServiceUrlException : IllegalArgumentException() {
    /**
     * Exception indicating that the provided URL does not use http or https protocol.
     *
     * This exception is thrown when a URL is provided that doesn't start with "http://" or "https://".
     *
     * ## Usage
     * ```kotlin
     * try {
 *     val url = ServiceUrl.from("ftp://example.com")
     * } catch (e: ServiceUrlException.InvalidUrl) {
     *     // Handle the invalid URL
     *     e.message // "The URL must start with http:// or https://"
     * }
     * ```
     */
    data object InvalidUrl : ServiceUrlException() {
        override val message: String
            get() = "The URL must start with http:// or https://"
    }
}
