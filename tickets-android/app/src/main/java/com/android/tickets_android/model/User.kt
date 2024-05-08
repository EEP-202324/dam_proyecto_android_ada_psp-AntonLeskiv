package com.android.tickets_android.model

data class User(
    val id: Long? = null,
    var role: Role,
    var email: String,
    var passwordHash: String,
    var firstName: String,
    var lastName: String
)
