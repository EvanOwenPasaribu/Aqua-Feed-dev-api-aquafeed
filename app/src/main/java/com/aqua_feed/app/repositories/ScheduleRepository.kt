package com.aqua_feed.app.repositories

import com.aqua_feed.app.models.request.ScheduleDeleteRequest
import com.aqua_feed.app.models.request.ScheduleRequest
import com.aqua_feed.app.models.request.ScheduleUpdateRequest
import com.aqua_feed.app.network.ApiService
import javax.inject.Inject


class ScheduleRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getAllSchedule() = api.getAllSchedule()
    suspend fun getAllNextSchedule() = api.getAllNextSchedule()
    suspend fun postSchedule(scheduleRequest: ScheduleRequest) = api.postSchedule(scheduleRequest)
    suspend fun postNextSchedule(scheduleRequest: ScheduleRequest) = api.postNextSchedule(scheduleRequest)
    suspend fun deleteSchedule(scheduleDeleteRequest: ScheduleDeleteRequest) = api.deleteSchedule(scheduleDeleteRequest)
    suspend fun deleteNextSchedule(scheduleDeleteRequest: ScheduleDeleteRequest) = api.deleteNextSchedule(scheduleDeleteRequest)
    suspend fun putSchedule(updateRequest: ScheduleUpdateRequest) = api.putSchedule(updateRequest)
    suspend fun putNextSchedule(updateRequest: ScheduleUpdateRequest) = api.putNextSchedule(updateRequest)

}