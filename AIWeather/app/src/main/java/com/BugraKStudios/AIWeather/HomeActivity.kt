package com.BugraKStudios.AIWeather

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.text.SpannableString
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.BugraKStudios.AIWeather.databinding.ActivityHomeBinding
import android.Manifest
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.BugraKStudios.AIWeather.repository.WeatherRepository
import com.BugraKStudios.AIWeather.retrofit.service.WeatherApi
import com.BugraKStudios.AIWeather.viewModel.WeatherViewModel
import com.BugraKStudios.AIWeather.viewModel.WeatherViewModelFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
    private lateinit var viewModel : WeatherViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = WeatherRepository(createWeatherApiService())
        val factory = WeatherViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        registerLauncher()

        //gradiant_temp
        val textView = binding.temp
        val text = textView.text.toString()
        val spannable = SpannableString(text)
        val gradient = LinearGradient(
            0f, 0f, textView.paint.measureText(text), textView.textSize,
            intArrayOf(Color.parseColor("#000AF0"), Color.parseColor("#00068C")),
            null,
            Shader.TileMode.CLAMP
        )

        if (textView.paint.shader == null) {
            textView.paint.shader = gradient
        }

        textView.text = spannable
        location()
        getCurrentDate()
    }

//Location Permission
    fun location() {
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                latitude = location.latitude
                longitude = location.longitude
                Log.d("location", "Latitude : ${location.latitude}")
                Log.d("location", "Longitude : ${location.longitude}")

                viewModel.getWeather(latitude, longitude)
                viewModel.weather.observe(this@HomeActivity, Observer { response ->
                    // Handle the response
                    val tempInt = response.main.temp.toInt()
                    Log.d("Weather", "Temperature: $tempInt , Description: ${response.weather.first().description}")
                    binding.cityName.setText(response.name)
                    binding.temp.setText(tempInt.toString()+"°")
                    binding.progrssName.visibility = View.GONE

                    //Ai request
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = "your_aistudio_api"
                    )

                    val prompt = "hava ${tempInt} derece ve ${response.weather.first().description} " +
                            "bugün bu hava durumuna göre nasıl giyinmeliyim 6 ya da 8 cümle ile açıkla cümlenin sonuna da (alt satıra geç ve iki satır boşluk bırak ) AIWeather tarafından önerildi diye ekle ('*' kullanma) "

                    val desc = "${response.weather.first().description} bunu türkçeye çevir"

                    lifecycleScope.launch {
                        val response = generativeModel.generateContent(desc)
                        response.text?.let { text ->
                            binding.desc.text = text
                        }

                    }

                    lifecycleScope.launch {
                        val response = generativeModel.generateContent(prompt)
                        response.text?.let { text ->
                            binding.textView.text = text
                        }
                        binding.progressText.visibility = View.GONE
                    }
                })

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Snackbar.make(
                    binding.root,
                    "Permission needed for location",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Give Permission") {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                //request permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            //permission granted
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000000,
                0f,
                locationListener
            )
        }

    }


    private fun registerLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000000,
                            0f,
                            locationListener
                        )
                    }
                } else {
                    Toast.makeText(this, "Permission needed!", Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun createWeatherApiService(): WeatherApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherApi::class.java)
    }

    fun getCurrentDate(): String {

        val calendar = Calendar.getInstance()
        
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // Gün ismi
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault()) // Gün numarası
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault()) // Ay ismi

        val day = dayFormat.format(calendar.time)
        val date = dateFormat.format(calendar.time)
        val month = monthFormat.format(calendar.time)

        binding.day.text = "${day}"
        binding.date.text = " ${ date}, "
        binding.month.text = "${ month}"
        // Formatlanmış tarihi döndür
        return "$day, $date $month"

    }
}
