package com.aquafeed.app.model.response

data class MessageStatusResponse(
    val code: Int,
    val msg: String,
    val reset: String
)

data class BaseMessageResponse(
    val response: String
)