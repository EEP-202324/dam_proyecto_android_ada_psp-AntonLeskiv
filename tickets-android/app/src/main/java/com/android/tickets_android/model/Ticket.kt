package com.android.tickets_android.model

data class Ticket(
    var id: Long? = null,
    var user: User,
    var event: Event,
    var uuid: String
)