package com.mvi.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.mvi.core.coroutines.DispatcherProvider
import com.mvi.core.environment.Environment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

/**
 * Factory for creating instances of [DataStore] backed by preferences.
 *
 * This class abstracts the initialization logic of DataStore, allowing for
 * different implementations based on the current [Environment]. It supports
 * mocking for unit tests by returning an [InMemoryDatastore] when [Environment.isMock]
 * is true, and a real [PreferenceDataStoreFactory] for production environments.
 *
 * @param environment The current environment configuration (e.g., MOCK, PRODUCTION).
 * @param dispatcherProvider The provider for coroutine dispatchers to ensure
 *                           asynchronous operations on IO.
 *
 */
class DatastoreFactory @Inject constructor(
    /**
     * The environment configuration to determine the DataStore type.
     * If set to `mock`, returns [InMemoryDatastore].
     * Otherwise, returns the standard preference [DataStore].
     */
    private val environment: Environment,
    /**
     * Provider for the CoroutineScope and Dispatcher.
     */
    private val dispatcherProvider: DispatcherProvider,
) {

    /**
     * Creates a new [DataStore] instance based on the current environment.
     *
     * @param args The arguments required to configure the DataStore (file name, migrations, context).
     * @return A valid [DataStore<Preferences>]. If the environment is `mock`,
     *         returns an in-memory store for testing purposes; otherwise, returns
     *         the production store.
     */
    fun create(args: DatastoreArgs): DataStore<Preferences> =
        when {
            environment.isMock -> InMemoryDatastore(args.datastore)
            else -> PreferenceDataStoreFactory.create(
                migrations = args.migrations,
                scope = dispatcherProvider.scope(),
                produceFile = {
                    // Ensures the file is created in the correct directory based on args
                    args.context.dataStoreFile(
                        fileName = args.datastoreName.value,
                    )
                },
            )
        }
}

/**
 * Generates a CoroutineScope for Datastore operations.
 *
 * DataStore requires a CoroutineScope to manage its asynchronous file I/O operations.
 * This function provides a scope that runs on the [io] dispatcher using a
 * [SupervisorJob]. The [SupervisorJob] ensures that if one coroutine child fails,
 * it does not cancel other children, allowing DataStore to handle individual
 * migration or write failures gracefully.
 *
 * @param io The IO dispatcher for disk operations.
 * @return A CoroutineScope suitable for Datastore.
 */
internal fun DispatcherProvider.scope(): CoroutineScope =
    CoroutineScope(io + SupervisorJob())
