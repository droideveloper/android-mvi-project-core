package com.mvi.core.database

import androidx.room.RoomDatabase

/**
 * Interface for defining a configuration action for a Room Database builder.
 * Represents a functional type that configures the database setup.
 *
 * @property builder A lambda to configure the RoomDatabase.Builder.
 */
interface DatabaseConfig : (RoomDatabase.Builder<*>) -> Unit
