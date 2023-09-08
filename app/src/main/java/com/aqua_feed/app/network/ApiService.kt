package com.aqua_feed.app.network

import com.aqua_feed.app.models.request.ControlBLDCRequest
import com.aqua_feed.app.models.request.LogRequest
import com.aqua_feed.app.models.request.MotoRequest
import com.aqua_feed.app.models.request.ParamsRequest
import com.aqua_feed.app.models.request.ScheduleDeleteRequest
import com.aqua_feed.app.models.request.ScheduleRequest
import com.aqua_feed.app.models.request.ScheduleUpdateRequest
import com.aquafeed.app.model.response.BaseMessageResponse
import com.aquafeed.app.model.response.DeviceResponse
import com.aquafeed.app.model.response.FeedingRequest
import com.aquafeed.app.model.response.LogResponseX
import com.aquafeed.app.model.response.MessageStatusResponse
import com.aquafeed.app.model.response.Params
import com.aquafeed.app.model.response.Schedule
import com.aquafeed.app.model.response.Sensor
import com.aquafeed.app.model.response.Time
import com.aquafeed.app.model.response.Wifi
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @GET("params")
    suspend fun getAllParam() : Response<Params>

    @PUT("params")
    suspend fun putParams(@Body paramsRequest: ParamsRequest): Response<Any>

    @GET("time")
    suspend fun getTime(): Response<Time>

    @GET("time")
    suspend fun postTime(@Body time: Time): Response<Any>

    @GET("schedule")
    suspend fun getAllSchedule(): Response<Schedule>

    @GET("nschedule")
    suspend fun getAllNextSchedule(): Response<Schedule>

    @POST("schedule")
    suspend fun postSchedule(@Body scheduleRequest: ScheduleRequest): Response<BaseMessageResponse>

    @POST("nschedule")
    suspend fun postNextSchedule(@Body scheduleRequest: ScheduleRequest): Response<BaseMessageResponse>

    @PUT("schedule")
    suspend fun putSchedule(@Body scheduleUpdateRequest: ScheduleUpdateRequest): Response<BaseMessageResponse>

    @PUT("nschedule")
    suspend fun putNextSchedule(@Body scheduleUpdateRequest: ScheduleUpdateRequest): Response<BaseMessageResponse>

    @POST("dschedule")
    suspend fun deleteSchedule(@Body scheduleDeleteRequest: ScheduleDeleteRequest): Response<BaseMessageResponse>

    @POST("ndschedule")
    suspend fun deleteNextSchedule(@Body scheduleDeleteRequest: ScheduleDeleteRequest): Response<BaseMessageResponse>

    @GET("wifi")
    suspend fun getWifi(): Response<Wifi>

    @PUT("wifi")
    suspend fun updateWifi(@Body wifi: Wifi): Response<Any>

    @POST("bldc")
    suspend fun startBldc(@Body bldcRequest: ControlBLDCRequest): Response<BaseMessageResponse>

    @POST("servo")
    suspend fun startServo(@Body bldcRequest: ControlBLDCRequest): Response<BaseMessageResponse>

    @GET("sensor")
    suspend fun getSensor(): Response<Sensor>

    @GET("status")
    suspend fun getStatus(): Response<MessageStatusResponse>

    @POST("servo")
    suspend fun posServoMotor(@Body request: MotoRequest): Response<BaseMessageResponse>

    @POST("bldc")
    suspend fun posBldcMotor(@Body request: MotoRequest): Response<BaseMessageResponse>

    @GET("log")
    suspend fun getLog(): Response<List<String>>

    @POST("log")
    suspend fun postLog(@Body logRequest: LogRequest): Response<List<String>>

    @GET("device")
    suspend fun getDevice(): Response<DeviceResponse>

    @POST("feeding")
    suspend fun postFeeding(@Body feedingRequest: FeedingRequest): Response<BaseMessageResponse>

}