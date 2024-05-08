package com.android.tickets_android.api

import com.android.tickets_android.model.AuthenticationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationService {
    @POST("/api/v1/auth/login")
    fun login(@Body credentials: Map<String, String>): Call<AuthenticationResponse>

    @POST("/api/v1/auth/register")
    fun register(@Body userData: Map<String, String>): Call<AuthenticationResponse>
}