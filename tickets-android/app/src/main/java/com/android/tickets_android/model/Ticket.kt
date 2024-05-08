package com.android.tickets_android.model

import java.util.UUID

data class Ticket(
    var id: Long? = null,
    var user: User,
    var event: Event,
    var uuid: String
)