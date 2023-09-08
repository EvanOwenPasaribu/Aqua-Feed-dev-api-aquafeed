package com.aqua_feed.app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aqua_feed.app.models.LogEntryEntity
import com.aqua_feed.app.models.LogEntryGroup

@Dao
interface LogEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogEntry(logEntry: LogEntryEntity)

    @Query("SELECT * FROM dialylog")
    suspend fun getAllLogEntries(): List<LogEntryEntity>

    @Query("SELECT date, schid, sum(kgamt) as kgamt FROM dialylog WHERE schid < 20 AND date = :dateText GROUP BY schid")
    suspend fun getLogEntriesByDateAndSchid(dateText: String): List<LogEntryGroup>

    @Query("SELECT * FROM dialylog WHERE date = :date  AND time = :time")
    suspend fun getLogEntryBySchidAndDate(date: String, time: String): LogEntryEntity?
}