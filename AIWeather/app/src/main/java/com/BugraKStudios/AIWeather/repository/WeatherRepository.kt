package com.BugraKStudios.AIWeather.repository

import com.BugraKStudios.AIWeather.retrofit.Weather
import com.BugraKStudios.AIWeather.retrofit.service.WeatherApi
import retrofit2.Response

class WeatherRepository(private val apiService: WeatherApi) {

    suspend fun getWeather(lat: Double, lon: Double): Response<Weather> {
        return apiService.getWeather(lat, lon)
    }
}