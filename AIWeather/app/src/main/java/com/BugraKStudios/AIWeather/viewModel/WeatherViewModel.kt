package com.BugraKStudios.AIWeather.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.BugraKStudios.AIWeather.repository.WeatherRepository
import com.BugraKStudios.AIWeather.retrofit.Weather
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather> = _weather

    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            val response = repository.getWeather(lat, lon)
            if (response.isSuccessful) {
                _weather.postValue(response.body())
            }
        }
    }
}