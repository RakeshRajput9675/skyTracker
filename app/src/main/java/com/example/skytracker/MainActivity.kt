package com.example.skytracker

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.skytracker.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val API_KEY = "bf04b734f0c137854b76bd98fc5db8f9"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        SearchCity()
    }

    private fun SearchCity() {
       val searchView = binding.searchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, API_KEY, "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val temperature = responseBody.main.temp.toString()
                        val humidity = responseBody.main.humidity.toString()
                        val windSpeed = responseBody.wind.speed.toString()
                        val sunRise = responseBody.sys.sunrise.toLong()
                        val sunSet = responseBody.sys.sunset.toLong()
                        val seaLevel = responseBody.main.pressure
                        val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                        val maxTemp = responseBody.main.temp_max
                        val minTemp = responseBody.main.temp_min


                        binding.temp.text = "$temperature °C"
                        binding.humidity.text = "$humidity %"
                        binding.windspeed.text = "$windSpeed m/s"
                        binding.sunrise.text = "${time(sunRise)}"
                        binding.sunset.text = "${time(sunSet)}"
                        binding.sea.text = "$seaLevel hPa"
                        binding.condition.text = "$condition"
                        binding.maxtemp.text = "Max Temp:$maxTemp °C"
                        binding.mintemp.text = "Min Temp:$minTemp °C"
                        binding.weather.text = condition
                        binding.day.text =dayName(System.currentTimeMillis())
                            binding.date.text =date()
                            binding.cityName.text =" $cityName"


                        changeImageAccordingToWeather(condition)
//                        Log.d("TAG", "$temperature")
                    }
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("WeatherApp", "Failed to fetch weather data", t)
                // Handle the error, e.g., show an error message to the user
            }
        })

    }

    private fun changeImageAccordingToWeather(conditions:String) {
          when(conditions){
              "Clear Sky", "Sunny", "Clear" ->{
                  binding.root.setBackgroundResource(R.drawable.sunny_background)
                  binding.lottieAnimationView.setAnimation(R.raw.sun)
              }
              "Partly clouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
                  binding.root.setBackgroundResource(R.drawable.colud_background)
                  binding.lottieAnimationView.setAnimation(R.raw.cloud)
              }
              "Light Rain", "Drizzle", "Moderate Rain","Showers", "Heavy Rain"->{
                  binding.root.setBackgroundResource(R.drawable.rain_background)
                  binding.lottieAnimationView.setAnimation(R.raw.rain)
              }
              "Light Snow", "Moderate Snow", "Heavy Snow","Blizzard"->{
                  binding.root.setBackgroundResource(R.drawable.snow_background)
                  binding.lottieAnimationView.setAnimation(R.raw.snow)
              }
else->{
    binding.root.setBackgroundResource(R.drawable.sunny_background)
    binding.lottieAnimationView.setAnimation(R.raw.sun)
}
          }
        binding.lottieAnimationView.playAnimation().toString()

    }

    private fun date(): String {
        val stf = SimpleDateFormat("dd MMMM yyyy ", Locale.getDefault())
        return stf.format((Date()))
    }

    fun dayName(timestamp:Long):String{
        val stf = SimpleDateFormat("EEEE", Locale.getDefault())
        return stf.format((Date()))
    }
    fun time(timestamp:Long):String{
        val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return stf.format((Date(timestamp*1000)))
    }

}
