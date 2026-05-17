package com.mvi.core.network

import com.mvi.core.kotlin.ValueObjectFactory

@JvmInline
value class ServiceUrl private constructor(
    val value: String
) {
    companion object : ValueObjectFactory<String, ServiceUrl, ServiceUrlException>() {

        override val initializer: (String) -> ServiceUrl = ::ServiceUrl
        override fun isValid(input: String): Boolean = when {
            input.startsWith("http://") || input.startsWith("https://") -> true
            else -> false
        }

        override fun getThrowable(input: String): ServiceUrlException? = when {
            isValid(input) -> null
            else -> ServiceUrlException.InvalidUrl
        }
    }
}

sealed class ServiceUrlException : IllegalArgumentException() {
    data object InvalidUrl : ServiceUrlException()
}
