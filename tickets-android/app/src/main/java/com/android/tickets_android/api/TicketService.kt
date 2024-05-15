package com.android.tickets_android.api

import com.android.tickets_android.model.PagedResponse
import com.android.tickets_android.model.Ticket
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TicketService {
    @GET("ticket")
    fun getAllTickets(): Call<List<Ticket>>

    @GET("ticket/{id}")
    fun getTicketById(@Path("id") id: Long): Call<Ticket>

    @GET("tickets/user/{userId}")
    suspend fun getTicketsByUserId(
        @Path("userId") userId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String>
    ): Response<PagedResponse<Ticket>>

    @POST("ticket")
    fun createTicket(
        @Query("userId") userId: Long,
        @Query("eventId") eventId: Long
    ): Call<Ticket>
}