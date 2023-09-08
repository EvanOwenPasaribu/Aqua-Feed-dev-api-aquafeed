package com.aqua_feed.app.models.request

data class ScheduleUpdateRequest(
    val schedDuration: Int,
    val schedId: Int,
    val schedKg: Double,
    val schedProgress: Int,
    val schedStatus: Boolean,
    val schedTime: String
)