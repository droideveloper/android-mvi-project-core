package com.mvi.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mvi.core.environment.Environment
import javax.inject.Inject

/**
 * Factory class responsible for constructing and configuring [RoomDatabase] instances.
 *
 * Determines whether to use an in-memory database or a file-based database based on the
 * current [Environment], and applies the provided configuration lambda.
 *
 * @param environment The environment context determining the build strategy.
 * @param config The configuration function to apply to the database builder.
 */
class RoomDatabaseFactory @Inject constructor(
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
     * @param args Wrapper for The Android [Context] and [DatabaseName]
     * @return A fully initialized [RoomDatabase.Builder] ready for type casting.
     * @see [DatabaseName]
     * @see [Context]
     */
    inline fun <reified Db: RoomDatabase> create(
        args: DatabaseArgs,
    ): RoomDatabase.Builder<Db> = when {
        environment.isMock -> Room.inMemoryDatabaseBuilder(args.context, Db::class.java)
        else -> Room.databaseBuilder(args.context, Db::class.java, args.databaseName.name)
    }
        .apply(config)
}
