package com.android.tickets_android.model

import java.time.LocalDateTime

data class Event(
    var id: Long,
    var name: String,
    var description: String,
    var place: String,
    var date: String
) {
    constructor(name: String, description: String, place: String, date: String) : this(
        0, name, description, place, date
    )
    constructor() : this(
        0, "", "", "", ""
    )
}
