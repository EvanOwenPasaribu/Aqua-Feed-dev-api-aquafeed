package com.aqua_feed.app.database

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AQFeedDatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application): AQFeedDatabase {
        return Room.databaseBuilder(application, AQFeedDatabase::class.java, "aqfeed_db")
            .build()
    }

    @Provides
    fun provideLogEntryDao(database: AQFeedDatabase): LogEntryDao {
        return database.logEntryDao()
    }
}