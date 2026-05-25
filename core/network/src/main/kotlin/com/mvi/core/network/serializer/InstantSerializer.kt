package com.mvi.core.network.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

/**
 * KSerializer implementation for Java's [Instant] value object.
 *
 * This serializer converts [Instant] instances to Unix epoch seconds (LONG)
 * for JSON serialization and back during deserialization. The representation
 * is compatible with the ISO-8601 date-time format used by most APIs.
 *
 * ## Usage
 * ```kotlin
 * @Serializable
 * data class Event(
 *     val timestamp: Instant = Instant.now()
 * )
 * ```
 *
 * ## Format
 * - **Serialization**: Epoch seconds as a 64-bit signed integer
 * - **Deserialization**: Epoch seconds converted to [Instant]
 * - **Timezone**: Uses UTC; no timezone offset is encoded
 *
 * ## Threading
 * Thread-safe singleton suitable for use in any context.
 *
 * @property descriptor Serial descriptor defining the LONG primitive type
 * @see Instant
 * @see KSerializer
 */
object InstantSerializer : KSerializer<Instant> {

    /**
     * Serial descriptor for [Instant].
     *
     * Defines the [Instant] serializer as a primitive LONG type.
     * The underlying JSON representation is an integer representing
     * Unix epoch seconds.
     */
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            serialName = "java.time.Instant",
            kind = PrimitiveKind.LONG,
        )

    /**
     * Encodes an [Instant] as its epoch seconds.
     *
     * Converts the [Instant] value to a 64-bit signed integer representing
     * the number of seconds since the Unix epoch (1970-01-01T00:00:00Z).
     * Only the epoch second component is encoded; nanoseconds are ignored.
     *
     * @param encoder The encoder to write the LONG value to
     * @param value The [Instant] to serialize
     */
    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.epochSecond)
    }

    /**
     * Decodes an [Instant] from its epoch seconds.
     *
     * Reads a 64-bit signed integer from the decoder and constructs an
     * [Instant] representing that number of seconds since the Unix epoch.
     *
     * @param decoder The decoder to read the LONG value from
     * @return An [Instant] constructed from the decoded epoch seconds
     */
    override fun deserialize(decoder: Decoder): Instant =
        Instant.ofEpochSecond(decoder.decodeLong())

}
