package com.aqua_feed.app.repositories

import com.aqua_feed.app.models.request.ControlBLDCRequest
import com.aqua_feed.app.network.ApiService
import com.aquafeed.app.model.response.FeedingRequest
import javax.inject.Inject

class MotorRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun startBldc( bldcRequest: ControlBLDCRequest) = apiService.startBldc(bldcRequest)

    suspend fun startServo( servoRequest: ControlBLDCRequest) = apiService.startServo(servoRequest)

    suspend fun postFeeding(feedingRequest: FeedingRequest) = apiService.postFeeding(feedingRequest)

}