package com.example.workmanager

import retrofit2.http.GET

data class SmartWatch(
    val id : Int,
    val name : String,
    val price : Double,
    val isWaterResistant : Boolean,
    val imageURL : String
)

interface ApiService {
    @GET("WatchAPI/readAll.php")
    fun getWatches():retrofit2.Call<List<SmartWatch>>
}