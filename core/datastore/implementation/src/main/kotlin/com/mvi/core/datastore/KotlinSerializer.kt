package com.mvi.core.datastore

import com.mvi.core.environment.Environment
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import javax.inject.Inject
import kotlin.reflect.KType

internal class KotlinSerializer @Inject constructor(
    private val environment: Environment,
    private val json: Json = Json {
        prettyPrint = environment.isDebug
        ignoreUnknownKeys = true
    },
) : Serializer {

    override fun <T> toString(type: KType, value: T): Result<String> = runCatching {
        json.encodeToString(
            serializer = json.serializersModule.serializer(type),
            value = value,
        )
    }

    override fun <T> fromString(type: KType, json: String): Result<T> = runCatching {
        @Suppress("UNCHECKED_CAST")
        val result = this.json.decodeFromString(
            deserializer = this.json.serializersModule.serializer(type),
            string = json,
        ) as? T
        checkNotNull(result) { "`$json` can not be deserialized to type `$type`." }
    }
}
