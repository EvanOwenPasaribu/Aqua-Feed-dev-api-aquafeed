package com.aquafeed.app.model.response

data class LogDataResponse(
    val schId: String,
    val subSchId: String,
    val time: String,
    val kg: Double
)