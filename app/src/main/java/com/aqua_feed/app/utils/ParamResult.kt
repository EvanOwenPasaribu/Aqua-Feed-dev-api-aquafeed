package com.aqua_feed.app.utils

import com.aquafeed.app.model.response.BaseMessageResponse
import com.aquafeed.app.model.response.DeviceResponse
import com.aquafeed.app.model.response.MessageStatusResponse
import com.aquafeed.app.model.response.Params
import com.aquafeed.app.model.response.Schedule
import com.aquafeed.app.model.response.Time
import com.aquafeed.app.model.response.Wifi

sealed class ParamResult {
    data class ParamsResponse(val params: Params) : ParamResult()
    data class TimeResponse(val time: Time) : ParamResult()
    data class ScheduleResponse(val schedule: Schedule) : ParamResult()
    data class WifiResponse(val wifi: Wifi) : ParamResult()
    data class StatusResponse(val status: MessageStatusResponse) : ParamResult()
    data class NextScheduleResponse(val nextSchedule: Schedule) : ParamResult()
    data class PostScheduleResponse(val message: BaseMessageResponse) : ParamResult()
    data class NextPostScheduleResponse(val message: BaseMessageResponse) : ParamResult()
    data class DeleteScheduleResponse(val message: BaseMessageResponse) : ParamResult()
    data class NextDeleteScheduleResponse(val message: BaseMessageResponse) : ParamResult()
    data class PutScheduleResponse(val message: BaseMessageResponse) : ParamResult()
    data class NextPutScheduleResponse(val message: BaseMessageResponse) : ParamResult()
    data class LogResponse(val log: List<String>) : ParamResult()
    data class ResponseDevice(val device: DeviceResponse) : ParamResult()
    data class PostLogResponse(val postLog: List<String>) : ParamResult()
    data class PostMotor(val motor: BaseMessageResponse) : ParamResult()
    data class Error(val message: String) : ParamResult()
}