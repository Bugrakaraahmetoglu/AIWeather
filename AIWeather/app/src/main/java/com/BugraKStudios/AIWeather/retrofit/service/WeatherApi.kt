package com.BugraKStudios.AIWeather.retrofit.service

import com.BugraKStudios.AIWeather.retrofit.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

        @GET("weather")
        suspend fun getWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("units") units: String = "metric",
            @Query("appid") apiKey: String = "00363d5f0c580f26584169714c73e4c5"
        ): Response<Weather>
    }