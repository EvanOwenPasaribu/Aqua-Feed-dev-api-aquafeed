package com.aqua_feed.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aqua_feed.app.models.LogEntryEntity
import com.aqua_feed.app.models.LogEntryGroup
import com.aqua_feed.app.models.request.LogRequest
import com.aqua_feed.app.repositories.LogRepository
import com.aqua_feed.app.utils.ParamResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val repo : LogRepository
): ViewModel() {
    private val _paramResult = MutableLiveData<ParamResult>()
    val paramResult: LiveData<ParamResult>
        get() = _paramResult

    init {
        getLog()
    }

    private fun getLog() = viewModelScope.launch {
        repo.getLog().handleResponse(ParamResult::LogResponse)
    }

    suspend fun getLogEntryBySchidAndDate(date: String, time: String): LogEntryEntity? {
        return repo.getLogEntryBySchidAndDate(date, time)
    }

    fun postLog(request: LogRequest) = viewModelScope.launch {
        repo.postLog(request).handleResponse (ParamResult::PostLogResponse)
    }

    suspend fun insertLogEntry(logEntry: LogEntryEntity) {
        repo.insertLogEntry(logEntry)
    }

    suspend fun getAllLogEntries() : List<LogEntryEntity>{
        return repo.getAllLogEntries()
    }

    suspend fun getLogEntriesByDateAndSchid(dateText: String): List<LogEntryGroup> {
        return repo.getLogEntriesByDateAndSchid(dateText)
    }

    private inline fun <T> Response<T>.handleResponse(resultType: (T) -> ParamResult) {
        if (isSuccessful) {
            body()?.let { data ->
                _paramResult.postValue(resultType(data))
            }
        } else {
            _paramResult.postValue(ParamResult.Error("Error: ${code()}"))
        }
    }
}