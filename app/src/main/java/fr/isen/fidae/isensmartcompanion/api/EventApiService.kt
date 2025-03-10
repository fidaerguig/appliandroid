package fr.isen.fidae.isensmartcompanion.api

import fr.isen.fidae.isensmartcompanion.models.Event
import retrofit2.Call
import retrofit2.http.GET

interface EventApiService {
    @GET("events.json")
    fun getEvents(): Call<List<Event>>
}
