package com.aqua_feed.app.models.request

data class ControlBLDCRequest(
    val duration: Int,
    val speed: Int,
    val status: Boolean
)