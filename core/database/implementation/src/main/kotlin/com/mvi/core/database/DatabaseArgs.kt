package com.mvi.core.database

import android.content.Context

data class DatabaseArgs(
    val context: Context,
    val databaseName: DatabaseName = DatabaseName.Default,
)
