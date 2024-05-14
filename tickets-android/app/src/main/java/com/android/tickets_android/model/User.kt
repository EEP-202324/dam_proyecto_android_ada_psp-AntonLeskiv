package com.android.tickets_android.model

data class User(
    val id: Long? = null,
    var role: Role,
    var email: String,
    var passwordHash: String,
    var firstName: String,
    var lastName: String
) {
    constructor(email: String, firstName: String, lastName: String) : this(
        null, Role.USER, email, null.toString(), firstName, lastName
    )
}