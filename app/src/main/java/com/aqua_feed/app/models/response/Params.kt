package com.aquafeed.app.model.response

data class Params(
    val batterythreshold: Int,
    val battstatus: Boolean,
    val bldcspeed: Int,
    val delaystart: Int,
    val delaystop: Int,
    val dprogress: Double,
    val ecstatus: Boolean,
    val endofdate: String,
    val feedrate: Int,
    val hallthreshold: Int,
    val hostname: String,
    val inastatus: Boolean,
    val lastfeed: String,
    val lastfeedamt: Double,
    val nextfeed: String,
    val nextfeedamt: Double,
    val phstatus: Boolean,
    val schdid: Int,
    val servospeed: Int,
    val solarpstatus: Boolean,
    val subschdid: Int,
    val system_start_date: String,
    val system_start_time: String,
    val tempstatus: Boolean,
    val throwamt: Int,
    val turbidstatus: Boolean
)