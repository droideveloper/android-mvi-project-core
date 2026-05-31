package com.mvi.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class KeyedValueDatastoreImpl @Inject constructor(
    private val datastore: Lazy<DataStore<Preferences>>,
    private val serializer: Serializer,
) : KeyedValueDatastore {

    override suspend fun <T> setValue(key: Result<DatastoreKey>, value: T): Result<Unit> =
        runCatching {
            val key = key.getOrThrow()
            datastore.value.edit {
                it[key.value] = value
            }
        }

    override suspend fun <T : Any> getValue(
        key: Result<DatastoreKey>,
        valueClass: KClass<T>,
    ): Result<T> = runCatching {
        val key = key.getOrThrow()
        getResult(key.value) {
            get(key.value, valueClass)
                ?: throw KeyedValueDatastoreException.NotFoundException(key.value)
        }
    }

    override fun <T : Any> getValueFlow(
        key: Result<DatastoreKey>,
        valueClass: KClass<T>,
    ): Flow<Result<T>> {
        val key = key.getOrThrow()
        return getResultFlow(key.value) {
            get(key.value, valueClass)
                ?: throw KeyedValueDatastoreException.NotFoundException(key.value)
        }
            .map { Result.success(it) }
            .catch { Result.failure<T>(it) }
    }

    override suspend fun <T> setSerializable(
        key: Result<DatastoreKey>,
        valueType: KType,
        value: T,
    ): Result<Unit> = runCatching {
        val key = key.getOrThrow()
        datastore.value.edit { it[key.value, valueType] = value }
    }

    override suspend fun <T : Any> getSerializable(
        key: Result<DatastoreKey>,
        valueType: KType,
    ): Result<T> = runCatching {
        val key = key.getOrThrow()
        getResult(key.value) {
            get<T>(key.value, valueType)
                ?: throw KeyedValueDatastoreException.NotFoundException(key.value)
        }
    }

    override fun <T : Any> getSerializableFlow(
        key: Result<DatastoreKey>,
        valueType: KType,
    ): Flow<Result<T>> {
        val key = key.getOrThrow()
        return getResultFlow(key.value) {
            get<T>(key.value, valueType)
                ?: throw KeyedValueDatastoreException.NotFoundException(key.value)
        }
            .map { Result.success(it) }
            .catch { Result.failure<T>(it) }
    }

    override suspend fun resetDatastore(datastoreName: String): Result<Unit> = runCatching {
        datastore
            .value
            .edit { prefs ->
                prefs
                    .getStoreKeys<Any>(datastoreName)
                    .forEach(prefs::remove)
            }
    }

    override suspend fun <T> removeValue(key: Result<DatastoreKey>, value: T): Result<Unit> = runCatching {
        val key = key.getOrThrow()
        if (value != null) throw IllegalArgumentException("value can not be non-null")
        datastore.value.edit { it[key.value] = value }
    }

    override suspend fun resetDefaultDatastore(): Result<Unit> = resetDatastore(
        datastoreName = DatastoreName.Default.name,
    )

    private fun <T> Preferences.get(key: String, type: KType): T? {
        return this[stringPreferencesKey(key)]
            ?.let { serializer.fromString<T>(type, it).getOrThrow() }
            ?: throw KeyedValueDatastoreException.NotFoundException(key)
    }

    private suspend fun <T> getResult(
        key: String,
        getter: Preferences.() -> T,
    ) = getResultFlow(key, getter)
        .first()

    private fun <T> getResultFlow(
        key: String,
        getter: Preferences.() -> T,
    ) = datastore
        .value
        .data
        .map { preferences ->
            getter(preferences)
                ?: throw KeyedValueDatastoreException.NotFoundException(key)
        }
        .catch { throw it }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> Preferences.get(key: String, clazz: KClass<T>): T? {
        return when (clazz) {
            Boolean::class -> this[booleanPreferencesKey(key)] as T
            Int::class -> this[intPreferencesKey(key)] as T
            Long::class -> this[longPreferencesKey(key)] as T
            Float::class -> this[floatPreferencesKey(key)] as T
            Double::class -> this[doublePreferencesKey(key)] as T
            String::class -> this[stringPreferencesKey(key)] as T
            ByteArray::class -> this[byteArrayPreferencesKey(key)] as T
            else -> null
        }
    }

    private operator fun <T> MutablePreferences.set(key: String, type: KType, value: T) {
        if (value == null) {
            this -= stringPreferencesKey(key)
        } else {
            this[stringPreferencesKey(key)] = serializer.toString(type, value).getOrThrow()
        }
    }

    private operator fun <T> MutablePreferences.set(key: String, value: T) {
        if (value == null) {
            this -= stringPreferencesKey(key)
        } else {
            when (value) {
                is Boolean -> this[booleanPreferencesKey(key)] = value
                is Int -> this[intPreferencesKey(key)] = value
                is Long -> this[longPreferencesKey(key)] = value
                is Float -> this[floatPreferencesKey(key)] = value
                is Double -> this[doublePreferencesKey(key)] = value
                is String -> this[stringPreferencesKey(key)] = value
                is ByteArray -> this[byteArrayPreferencesKey(key)] = value
                else -> throw IllegalArgumentException("Cannot set value for key `$key = $value`")
            }
        }
    }

    private fun <T : Any> Preferences.getStoreKeys(storeKey: String): List<Preferences.Key<T>> {
        return asMap()
            .keys
            .asSequence()
            .filter { key ->
                DatastoreKey.get(key.name)
                    .fold(
                        onSuccess = { it.value == storeKey },
                        onFailure = { false },
                    )
            }
            .map {
                @Suppress("UNCHECKED_CAST")
                it as? Preferences.Key<T>
            }
            .filterNotNullTo(mutableListOf())
    }
}
