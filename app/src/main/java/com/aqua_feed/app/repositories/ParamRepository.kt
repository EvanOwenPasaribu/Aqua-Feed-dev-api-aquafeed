package com.aqua_feed.app.repositories

import com.aqua_feed.app.network.ApiService
import javax.inject.Inject

class ParamRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getAllParam() = api.getAllParam()

    suspend fun getTime() = api.getTime()

    suspend fun getWifi() = api.getWifi()

    suspend fun getStatus() = api.getStatus()

    suspend fun getDevice() = api.getDevice()
}