package com.android.tickets_android.api

import com.android.tickets_android.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @GET("user")
    fun getAllUsers(): Call<List<User>>

    @GET("user/{id}")
    fun getUserById(@Path("id") id: Long): Call<User>

    @POST("user")
    fun createUser(@Body user: User): Call<User>

    @POST("user/searchByEmail")
    fun getUserByEmail(@Body emailMap: Map<String, String>): Call<User>

    @PUT("user/{id}")
    fun updateUser(@Path("id") id: Long, @Body user: User): Call<User>

    @DELETE("user/{id}")
    fun deleteUser(@Path("id") id: Long): Call<Void>
}