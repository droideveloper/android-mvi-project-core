package com.mvi.core.datastore

import kotlin.reflect.KType

/**
 * Defines the contract for serializing and deserializing data objects.
 *
 * This interface provides a standard mechanism for converting between
 * Kotlin data objects and their JSON string representations using a type-safe approach.
 * It supports both serialization (object to string) and deserialization (string to object)
 * operations with comprehensive error handling via the [Result] type.
 *
 * This interface is typically implemented by serializers that work with Kotlin metadata
 * (KType) to support advanced type introspection, polymorphism, and custom serialization
 * logic. It ensures that serialization/deserialization operations remain
 * type-safe and consistent throughout the application.
 *
 * @param KType Represents the Kotlin type metadata used for type-aware operations.
 *              Enables proper handling of type parameters such as data classes,
 *              enums, and nested generics.
 * @param Result<T> Contains either the serialized/deserialized value or an error message
 *                  indicating the failure reason (e.g., type not supported, malformed JSON).
 *
 */
interface Serializer {

    /**
     * Serializes a given value of the specified type into its JSON string representation.
     *
     * Converts a Kotlin object of type `T` into a JSON string, using the provided
     * `KType` metadata to maintain type information during the transformation. This is
     * particularly useful for complex object graphs and nested structures.
     *
     * @param type The [KType] metadata that describes the type of the value to be serialized.
     *             This is essential for correct type-safe serialization and for handling
     *             generic parameters and polymorphic types.
     * @param value The object to be serialized into a JSON string.
     * @return A [Result] containing the serialized JSON string on success.
     *         On failure, returns an error value with a descriptive message.
     * @throws IllegalArgumentException If the value is null or if the type is unsupported.
     * @see fromString
     */
    fun <T> toString(type: KType, value: T): Result<String>

    /**
     * Deserializes a JSON string into a value of the specified type.
     *
     * Parses the provided JSON string and reconstructs an object of type `T`
     * using the [KType] metadata to maintain type safety. This is useful for
     * populating data models from external data sources like API responses, files,
     * or user input.
     *
     * @param type The [KType] metadata that specifies the expected target type for deserialization.
     *             Required to validate type safety during the parsing process.
     * @param json The JSON string representation of the target object to deserialize.
     * @return A [Result] containing the deserialized object of type `T` on success.
     *         On failure, returns an error value describing why deserialization failed.
     * @throws IllegalArgumentException If the JSON is malformed, the type is unsupported,
     *                                  or a type mismatch is detected.
     * @see toString
     */
    fun <T> fromString(type: KType, json: String): Result<T>
}
