package com.aqua_feed.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aqua_feed.app.models.request.ScheduleDeleteRequest
import com.aqua_feed.app.models.request.ScheduleRequest
import com.aqua_feed.app.models.request.ScheduleUpdateRequest
import com.aqua_feed.app.repositories.ScheduleRepository
import com.aqua_feed.app.utils.ParamResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repo: ScheduleRepository
): ViewModel() {

    private val _paramResult = MutableLiveData<ParamResult>()
    val paramResult: LiveData<ParamResult>
        get() = _paramResult


    fun getAllSchedule() = viewModelScope.launch {
        repo.getAllSchedule().handleResponse(ParamResult::ScheduleResponse)
    }

    fun getAllNextSchedule() = viewModelScope.launch {
        repo.getAllNextSchedule().handleResponse(ParamResult::NextScheduleResponse)
    }

    fun postSchedule(scheduleRequest: ScheduleRequest) = viewModelScope.launch {
        repo.postSchedule(scheduleRequest).handleResponse(ParamResult::PostScheduleResponse)
    }

    fun postNextSchedule(scheduleRequest: ScheduleRequest) = viewModelScope.launch {
        repo.postNextSchedule(scheduleRequest).handleResponse(ParamResult::NextPostScheduleResponse)
    }

    fun deleteSchedule(scheduleDeleteRequest: ScheduleDeleteRequest) = viewModelScope.launch {
        repo.deleteSchedule(scheduleDeleteRequest).handleResponse(ParamResult::DeleteScheduleResponse)
    }

    fun deleteNextSchedule(scheduleDeleteRequest: ScheduleDeleteRequest) = viewModelScope.launch {
        repo.deleteNextSchedule(scheduleDeleteRequest).handleResponse(ParamResult::NextDeleteScheduleResponse)
    }

    fun putSchedule(updateRequest: ScheduleUpdateRequest) = viewModelScope.launch {
        repo.putSchedule(updateRequest).handleResponse(ParamResult::PutScheduleResponse)
    }

    fun putNextSchedule(updateRequest: ScheduleUpdateRequest) = viewModelScope.launch {
        repo.putNextSchedule(updateRequest).handleResponse(ParamResult::NextPutScheduleResponse)
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