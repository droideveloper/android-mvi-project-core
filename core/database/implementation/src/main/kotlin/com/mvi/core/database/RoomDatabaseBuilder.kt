package com.mvi.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mvi.core.environment.Environment
import javax.inject.Inject

/**
 * Builder class responsible for constructing and configuring [RoomDatabase] instances.
 *
 * Determines whether to use an in-memory database or a file-based database based on the
 * current [Environment], and applies the provided configuration lambda.
 *
 * @param environment The environment context determining the build strategy.
 * @param config The configuration function to apply to the database builder.
 */
class RoomDatabaseBuilder @Inject constructor(
    /**
     * The environment context used to decide between mock and real database building.
     */
    val environment: Environment,
    /**
     * The functional configuration to apply to the Room database builder.
     */
    val config: DatabaseConfig,
) {

    /**
     * Creates and initializes a builder for the specified database type.
     *
     * If the environment is in mock mode, it uses [Room.inMemoryDatabaseBuilder].
     * Otherwise, it uses [Room.databaseBuilder] with the provided database name.
     * The configuration is then applied via the [config] lambda.
     *
     * @param context The Android [Context] required for database creation.
     * @param databaseName The validated [DatabaseName] for the database file.
     * @return A fully initialized [RoomDatabase.Builder] ready for type casting.
     */
    inline fun <reified Db: RoomDatabase> create(
        context: Context,
        databaseName: DatabaseName,
    ): RoomDatabase.Builder<Db> = when {
        environment.isMock -> Room.inMemoryDatabaseBuilder(context, Db::class.java)
        else -> Room.databaseBuilder(context, Db::class.java, databaseName.value)
    }
        .apply(config)
}
