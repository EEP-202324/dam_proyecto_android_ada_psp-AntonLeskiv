package com.android.tickets_android.model

data class AuthenticationResponse(
    val success: Boolean,
    val message: String,
    val userId: Long? = null,
    val role: String? = null
)
