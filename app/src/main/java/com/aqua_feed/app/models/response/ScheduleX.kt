package com.aquafeed.app.model.response

data class ScheduleX(
    val schedDuration: Int,
    val schedId: Int,
    val schedKg: Double,
    val schedProgress: Int,
    val schedStatus: Boolean,
    val schedTime: String
)