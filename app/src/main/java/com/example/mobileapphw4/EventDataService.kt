package com.example.mobileapphw4

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EventDataService {

    @GET("discovery/v2/events.json")
    fun getEventNameByCity(@Query("city") city: String,
                           @Query("apikey") apiKey: String): Call<TicketData>
}