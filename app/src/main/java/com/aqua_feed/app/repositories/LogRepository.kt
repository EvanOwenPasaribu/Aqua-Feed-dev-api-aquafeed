package com.aqua_feed.app.repositories

import com.aqua_feed.app.database.LogEntryDao
import com.aqua_feed.app.models.LogEntryEntity
import com.aqua_feed.app.models.LogEntryGroup
import com.aqua_feed.app.models.request.LogRequest
import com.aqua_feed.app.network.ApiService
import javax.inject.Inject

class LogRepository @Inject constructor(
    private val api: ApiService,
    private val logEntryDao: LogEntryDao
) {
    suspend fun getLog() = api.getLog()

    suspend fun postLog(request: LogRequest) = api.postLog(request)

    suspend fun getLogEntriesByDateAndSchid(dateText: String): List<LogEntryGroup> {
        return logEntryDao.getLogEntriesByDateAndSchid(dateText)
    }

    suspend fun getAllLogEntries(): List<LogEntryEntity> {
        return logEntryDao.getAllLogEntries()
    }

    suspend fun insertLogEntry(logEntry: LogEntryEntity) {
        logEntryDao.insertLogEntry(logEntry)
    }

    suspend fun getLogEntryBySchidAndDate(date: String, time: String): LogEntryEntity? {
        return logEntryDao.getLogEntryBySchidAndDate( date, time)
    }
}