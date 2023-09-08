package com.aquafeed.app.model.response

data class Wifi(
    val Hostname: String,
    val wifSTAiIP: String,
    val wifiIP: String,
    val wifiMode: String,
    val wifiPASS: String,
    val wifiSSID: String,
    val wifiSTAPASS: String,
    val wifiSTARSSI: Int,
    val wifiSTASSID: String,
    val wifiTYPE: Boolean
)