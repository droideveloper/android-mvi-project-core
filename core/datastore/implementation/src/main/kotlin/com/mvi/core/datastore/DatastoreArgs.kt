package com.mvi.core.datastore

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences

/**
 * Data class containing all required arguments to instantiate a [DataStore].
 *
 * This class acts as the primary request payload for the [DatastoreFactory.create]
 * function. It encapsulates the necessary configuration information to either
 * create an in-memory store for testing, or a persistent preference store
 * for production environments. This includes the file name, migration strategies,
 * and initial state data.
 *
 * @property context Application context required to locate the DataStore file
 *                  directory and retrieve application-level storage permissions.
 * @property datastoreName The unique name for the DataStore. If not provided,
 *                         defaults to [DatastoreName.Default]. Use this to manage
 *                         multiple preference stores within the same app.
 * @property migrations A list of migration strategies to apply when initializing
 *                      the DataStore. These are applied sequentially during the
 *                      first write operation. Defaults to [emptyList()] if not
 *                      specified, meaning no migration logic will be applied.
 * @property datastore Initial data map to populate the store upon creation.
 *                      This is useful for seeding defaults or preloading
 *                      user preferences during app startup. Defaults to an
 *                      empty [MutableMap].
 *
 */
data class DatastoreArgs(
    /**
     * The application context required to locate the DataStore file directory
     * and handle file permissions for persistent storage.
     *
     * Must be a valid Application Context (or Activity Context wrapped in an
     * Application Context) to ensure proper file location resolution and
     * resource access.
     *
     * @throws IllegalStateException if context is null or invalid for production use.
     */
    val context: Context,
    /**
     * The unique identifier for the DataStore. This name is used to locate
     * and create the corresponding XML file on the device.
     *
     * Defaults to [DatastoreName.Default] if not explicitly provided.
     */
    val datastoreName: DatastoreName = DatastoreName.Default,
    /**
     * List of migration strategies to apply when the DataStore is first initialized.
     *
     * If the DataStore file exists and the schema has changed since the last
     * launch, these migrations will be applied automatically.
     *
     * Default: [emptyList()] - No migrations will be performed.
     */
    val migrations: List<DataMigration<Preferences>> = emptyList(),
    /**
     * Initial key-value pairs to populate the DataStore with upon first creation.
     *
     * This is useful for setting default values or populating initial state.
     * Note: These values may be overridden by migrations during the first write.
     *
     * Default: [mutableMapOf()] - Empty map if not specified.
     */
    val datastore: MutableMap<Preferences.Key<*>, Any> = mutableMapOf(),
)


