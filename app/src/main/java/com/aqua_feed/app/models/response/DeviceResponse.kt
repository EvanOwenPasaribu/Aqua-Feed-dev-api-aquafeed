package com.aquafeed.app.model.response

data class DeviceResponse(
    val hardwareVersion: Int,
    val serial_number: String,
    val softwareVersion: Int
)