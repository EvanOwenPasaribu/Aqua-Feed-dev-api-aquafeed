package com.aqua_feed.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aqua_feed.app.repositories.ParamRepository
import com.aqua_feed.app.utils.ParamResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ParamViewModel @Inject constructor(
    private val repo: ParamRepository
) : ViewModel() {
    private val _paramResult = MutableLiveData<ParamResult>()
    val paramResult: LiveData<ParamResult>
        get() = _paramResult

    init {
        getAllParam()
        getTimes()
        getWifi()
        getStatus()
        getDevice()
    }

    private fun getAllParam() = viewModelScope.launch {
        repo.getAllParam().handleResponse(ParamResult::ParamsResponse)
    }

    private fun getDevice() = viewModelScope.launch {
        repo.getDevice().handleResponse(ParamResult::ResponseDevice)
    }

    private fun getTimes() = viewModelScope.launch {
        repo.getTime().handleResponse(ParamResult::TimeResponse)
    }

    private fun getWifi() = viewModelScope.launch {
        repo.getWifi().handleResponse(ParamResult::WifiResponse)
    }

    private fun getStatus() = viewModelScope.launch {
        repo.getStatus().handleResponse(ParamResult::StatusResponse)
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
