package com.android.tickets_android.model

import java.time.LocalDateTime

data class Event(
    var id: Long? = null,
    var name: String,
    var description: String,
    var date: String,
    var place: String
)
