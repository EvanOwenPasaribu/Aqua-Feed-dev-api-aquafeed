package com.aqua_feed.app.models

import java.util.UUID

data class User(
    var id : String ="",
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    var password: String = ""
)
