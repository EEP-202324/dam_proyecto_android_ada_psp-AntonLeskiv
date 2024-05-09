package com.android.tickets_android.api

import com.android.tickets_android.model.Event
import com.android.tickets_android.model.PagedResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventService {
    @GET("event")
    suspend fun getAllEvents(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<PagedResponse<Event>>

    @GET("event/{id}")
    suspend fun getEventById(@Path("id") id: Long): Call<Event>

    @POST("event")
    suspend fun createEvent(@Body event: Event): Response<Event>

    @DELETE("event/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Response<Void>
}