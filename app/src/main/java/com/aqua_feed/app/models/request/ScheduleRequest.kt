package com.aqua_feed.app.models.request

data class ScheduleRequest(
    val schedDuration: Int,
    val schedKg: Double,
    val schedStatus: Boolean,
    val schedTime: String
)