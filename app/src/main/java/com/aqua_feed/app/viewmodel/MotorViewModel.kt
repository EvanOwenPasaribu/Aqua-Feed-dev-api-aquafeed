package com.aqua_feed.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aqua_feed.app.models.request.ControlBLDCRequest
import com.aqua_feed.app.repositories.MotorRepository
import com.aqua_feed.app.utils.ParamResult
import com.aquafeed.app.model.response.FeedingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MotorViewModel @Inject constructor(
    private val motorRepository: MotorRepository
): ViewModel() {

    private val _paramResult = MutableLiveData<ParamResult>()
    val paramResult: LiveData<ParamResult>
        get() = _paramResult


    fun startBldc(bldcRequest: ControlBLDCRequest) = viewModelScope.launch {
        motorRepository.startBldc(bldcRequest).handleResponse(ParamResult::PostMotor)
    }

    fun startServo(servoRequest: ControlBLDCRequest) = viewModelScope.launch {
        motorRepository.startServo(servoRequest).handleResponse(ParamResult::PostMotor)
    }

    fun postFeeding(feedingRequest: FeedingRequest) = viewModelScope.launch {
        motorRepository.postFeeding(feedingRequest).handleResponse(ParamResult::PostMotor)
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