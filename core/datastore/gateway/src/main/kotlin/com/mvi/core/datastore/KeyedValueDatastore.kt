package com.mvi.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A high-level interface for reading and writing typed data to a key-value datastore.
 *
 * This interface provides a fluent API for storing, retrieving, and removing typed values
 * from a datastore. It supports both synchronous operations and reactive streams
 * (via [Flow]) to handle asynchronous operations and real-time updates.
 *
 * The API is designed to be type-safe, using [KClass] and [KType] to ensure that
 * the correct values are stored and retrieved for each key. All operations return
 * a [Result] wrapper to handle errors gracefully without throwing unchecked exceptions.
 *
 * ## Usage Pattern
 *
 * Use [getValue] or [setValue] for standard type-safe operations. Use
 * [getSerializable] or [setSerializable] when working with custom types
 * that require additional serialization logic or KType metadata.
 *
 * The interface accepts [Result<DatastoreKey>] as a key, allowing callers to
 * handle key validation and construction outside the datastore operation
 * itself.
 *
 * @property KeyedValueDatastore Provides a contract for all CRUD operations
 *                                on a key-value datastore with typed support.
 * @property Result<T> All methods return a [Result] to indicate success or failure,
 *                      with [Unit] on success for write operations.
 * @property Flow<Result<T>> Reactive streams allowing listeners to observe
 *                              datastore changes over time.
 * @see DatastoreKey
 * @see DatastoreName
 * @see KeyedValueDatastoreException
 */
interface KeyedValueDatastore {

    /**
     * Stores a typed value into the datastore under the specified key.
     *
     * Performs an asynchronous write operation to store a value of type `T`
     * associated with the provided key. The key must be a valid [DatastoreKey]
     * as described by [DatastoreKey.isValid].
     *
     * @param key The [Result<DatastoreKey>] containing the datastore key.
     *            If the key result contains an error, it propagates immediately.
     * @param value The object to store in the datastore. The type `T` must be
     *              supported by the datastore's serializer or encoding logic.
     * @return A [Result] indicating whether the value was successfully stored.
     *         On success, the result contains [Unit]. On failure, the [Result]
     *         contains the error message (e.g., "Key not found", "Write timeout", etc.).
     * @see getValue
     */
    suspend fun <T> setValue(key: Result<DatastoreKey>, value: T): Result<Unit>

    /**
     * Retrieves a typed value from the datastore under the specified key.
     *
     * Reads a value of type `T` from the datastore. The caller must specify
     * the expected type using [KClass<T>]. This method is useful when the
     * datastore schema and types are known at compile-time or runtime via
     * reflection.
     *
     * @param key The [Result<DatastoreKey>] containing the datastore key.
     * @param valueClass The [KClass] of the expected value type to read.
     *                   Used for deserialization and type checking.
     * @return A [Result<T>] containing the retrieved value on success.
     *         On failure, returns an error result (e.g., "Key not found").
     * @throws IllegalArgumentException If [key] or [valueClass] is null.
     * @see getValueFlow
     * @see setSerializable
     */
    suspend fun <T : Any> getValue(key: Result<DatastoreKey>, valueClass: KClass<T>): Result<T>

    /**
     * Retrieves a typed value from the datastore as a reactive [Flow].
     *
     * Provides a reactive stream that emits [Result<T>] values whenever the
     * datastore updates or when the client observes changes. This is useful
     * for real-time data synchronization, caching, or reactive UI updates.
     *
     * Note that the returned flow may emit an error result if the key is not
     * found or if a read operation fails. It does not automatically retry.
     *
     * @param key The [Result<DatastoreKey>] containing the datastore key.
     * @param valueClass The [KClass] of the expected value type to read.
     * @return A cold [Flow<Result<T>>] that emits the retrieved value or
     *         an error on each successful read attempt.
     * @see getValue
     * @see getValueFlow
     */
    fun <T : Any> getValueFlow(key: Result<DatastoreKey>, valueClass: KClass<T>): Flow<Result<T>>

    /**
     * Stores a value with explicit [KType] serialization metadata.
     *
     * Similar to [setValue], but allows you to specify the serialization
     * type explicitly. This is useful for polymorphic serialization, nested
     * generic types, or when using custom serialization logic.
     *
     * @param key The [Result<DatastoreKey>] containing the datastore key.
     * @param valueType The [KType] metadata specifying the serialization type.
     * @param value The object to serialize and store in the datastore.
     * @return A [Result] indicating success or failure of the write operation.
     * @see getSerializable
     * @see setSerializable
     */
    suspend fun <T> setSerializable(key: Result<DatastoreKey>, valueType: KType, value: T): Result<Unit>

    /**
     * Retrieves a value with explicit [KType] deserialization metadata.
     *
     * Similar to [getValue], but allows you to specify the deserialization
     * type explicitly. This is useful for handling polymorphic types or
     * custom serialization formats where the default [KClass] detection
     * is insufficient.
     *
     * @param key The [Result<DatastoreKey>] containing the datastore key.
     * @param valueType The [KType] metadata specifying the deserialization type.
     * @return A [Result<T>] containing the deserialized value on success.
     * @throws IllegalArgumentException If [valueType] is null or unsupported.
     * @see getSerializableFlow
     */
    suspend fun <T : Any> getSerializable(key: Result<DatastoreKey>, valueType: KType): Result<T>

    /**
     * Retrieves a value as a reactive [Flow] with explicit [KType] deserialization.
     *
     * Similar to [getValueFlow], but uses explicit [KType] for deserialization.
     * This is useful for real-time reading of polymorphic or custom serialized data.
     *
     * @param key The [Result<DatastoreKey>] containing the datastore key.
     * @param valueType The [KType] metadata specifying the deserialization type.
     * @return A [Flow<Result<T>>] emitting values or errors.
     * @see getValueFlow
     */
    fun <T : Any> getSerializableFlow(key: Result<DatastoreKey>, valueType: KType): Flow<Result<T>>

    /**
     * Removes a value from the datastore under the specified key.
     *
     * Performs an asynchronous delete operation. If the value exists and is
     * successfully removed, the result is [Unit] wrapped in a success [Result].
     * If the value does not exist or the operation fails, the error is returned.
     *
     * @param key The [Result<DatastoreKey>] containing the datastore key.
     * @param value The optional value to verify before deletion. If provided,
     *              the datastore may perform a "delete if match" operation
     *              to prevent accidental removal of unrelated values.
     * @return A [Result] indicating whether the value was successfully removed.
     */
    suspend fun <T> removeValue(key: Result<DatastoreKey>, value: T): Result<Unit>

    /**
     * Resets a specific datastore by name.
     *
     * Clears all data associated with the given [datastoreName]. This is typically
     * used for migration, testing, or recovery operations. Use with caution in
     * production environments where data persistence is expected.
     *
     * @param datastoreName The name of the datastore to reset.
     * @return A [Result] indicating whether the reset operation succeeded.
     *         On success, the result contains [Unit].
     * @throws IllegalArgumentException If [datastoreName] is invalid or null.
     * @see resetDefaultDatastore
     */
    suspend fun resetDatastore(datastoreName: String): Result<Unit>

    /**
     * Resets the default datastore.
     *
     * Clears all data from the default datastore. This is useful for initializing
     * a new application state, testing, or recovery scenarios. The exact behavior
     * depends on the underlying datastore implementation.
     *
     * @return A [Result] indicating whether the reset operation succeeded.
     *         On success, the result contains [Unit].
     * @see resetDatastore
     */
    suspend fun resetDefaultDatastore(): Result<Unit>
}

/**
 * A convenience extension function to retrieve a reified value of type `T`
 * from the datastore using only the key.
 *
 * This removes the need to explicitly pass `T::class` to [getValue]. The
 * reified type parameter is captured at the call site, making the API
 * cleaner and more idiomatic.
 *
 * @param key The [Result<DatastoreKey>] containing the datastore key.
 * @return A [Result<T>] containing the retrieved value on success.
 * @see getValue
 * @see KeyedValueDatastore.getValueFlow
 */
suspend inline fun <reified T : Any> KeyedValueDatastore.getValue(key: Result<DatastoreKey>): Result<T> =
    getValue(key, T::class)

/**
 * A convenience extension function to retrieve a reified flow of type `T`.
 *
 * Similar to [KeyedValueDatastore.getValueFlow], but allows you to omit
 * the explicit [KClass] parameter for cleaner call sites. The flow
 * emits [Result<T>] values for real-time updates.
 *
 * @param key The [Result<DatastoreKey>] containing the datastore key.
 * @return A [Flow<Result<T>>] emitting values or errors.
 * @see KeyedValueDatastore.getValueFlow
 * @see getValue
 */
inline fun <reified T : Any> KeyedValueDatastore.getValueFlow(key: Result<DatastoreKey>): Flow<Result<T>> =
    getValueFlow(key, T::class)

/**
 * A convenience extension function to set a reified value of type `T`
 * using explicit serialization metadata.
 *
 * Similar to [setSerializable], but uses `typeOf<T>()` to capture the
 * reified type parameter automatically. Useful for reducing boilerplate
 * when working with multiple types.
 *
 * @param key The [Result<DatastoreKey>] containing the datastore key.
 * @param value The object to serialize and store.
 * @return A [Result] indicating success or failure of the write operation.
 * @see setSerializable
 */
suspend inline fun <reified T : Any> KeyedValueDatastore.setSerializable(key: Result<DatastoreKey>, value: T): Result<Unit> =
    setSerializable(key, typeOf<T>(), value)

/**
 * A convenience extension function to retrieve a reified value with explicit
 * serialization metadata.
 *
 * Similar to [getSerializable], but uses `typeOf<T>()` to capture the
 * reified type parameter automatically. Useful for deserializing custom types
 * where the KType is inferred from the reified parameter.
 *
 * @param key The [Result<DatastoreKey>] containing the datastore key.
 * @return A [Result<T>] containing the deserialized value on success.
 * @see getSerializable
 */
suspend inline fun <reified T : Any> KeyedValueDatastore.getSerializable(key: Result<DatastoreKey>): Result<T> =
    getSerializable(key, typeOf<T>())

/**
 * A convenience extension function to retrieve a reified flow with explicit
 * serialization metadata.
 *
 * Similar to [getSerializableFlow], but uses `typeOf<T>()` to capture the
 * reified type parameter automatically.
 *
 * @param key The [Result<DatastoreKey>] containing the datastore key.
 * @return A [Flow<Result<T>>] emitting values or errors.
 * @see getSerializableFlow
 */
inline fun <reified T : Any> KeyedValueDatastore.getSerializableFlow(key: Result<DatastoreKey>): Flow<Result<T>> =
    getSerializableFlow(key, typeOf<T>())

/**
 * Exception types associated with [KeyedValueDatastore] violations.
 *
 * This sealed class represents errors that occur when a datastore operation
 * fails. It extends [IllegalArgumentException] to signal that an invalid
 * operation was performed or the datastore returned an unexpected error.
 *
 * Subclasses of this exception provide specific context about the failure,
 * such as a missing key, read/write errors, or serialization issues.
 *
 */
sealed class KeyedValueDatastoreException : IllegalArgumentException() {

    /**
     * Exception indicating that a requested key was not found in the datastore.
     *
     * This exception is typically thrown when a read operation fails because
     * the key does not exist. It is useful for handling "not found" errors
     * gracefully and distinguishing them from other failure modes.
     *
     * @property message A descriptive message containing the missing key
     *                    or context about why the value was not found.
     *
     * @see KeyedValueDatastoreException
     * @see KeyedValueDatastore
     */
    data class NotFoundException(override val message: String?) : KeyedValueDatastoreException()
}

