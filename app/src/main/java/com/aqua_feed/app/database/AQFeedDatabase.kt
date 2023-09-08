package com.aqua_feed.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aqua_feed.app.models.LogEntryEntity

    @Database(entities = [LogEntryEntity::class], version = 1, exportSchema = false)
    abstract class AQFeedDatabase : RoomDatabase() {
        abstract fun logEntryDao(): LogEntryDao
    }