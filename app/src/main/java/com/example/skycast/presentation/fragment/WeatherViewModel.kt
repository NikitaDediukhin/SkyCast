package com.example.skycast.presentation.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.WeatherModel
import com.example.domain.usecase.GetWeatherDataUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel class for managing weather data and handling UI-related logic.
 * This ViewModel interacts with the WeatherRepository through GetWeatherDataUseCase
 * to fetch weather information based on the city and permissions.
 */
class WeatherViewModel(
    private val getWeatherDataUseCase: GetWeatherDataUseCase
): ViewModel() {

    // LiveData to hold the weather data fetched from the repository
    private val weatherDataMutable = MutableLiveData<WeatherModel>()
    val weatherDataLive: LiveData<WeatherModel> = weatherDataMutable

    // LiveData to hold the selected weather day chosen by the user
    private val selectedWeatherDayMutable = MutableLiveData<WeatherModel.DailyWeather?>()
    val selectedWeatherDayLive: LiveData<WeatherModel.DailyWeather?> = selectedWeatherDayMutable

    // LiveData to hold the current city name
    private val cityNameMutable = MutableLiveData<String>()

    // LiveData to hold the status of permissions and location
    private val isPermissionAndLocationGranted = MutableLiveData<Boolean>()

    /**
     * Function to fetch weather data based on the current city and permission status.
     * It launches a coroutine in the ViewModel scope to execute the getWeatherDataUseCase.
     */
    private fun fetchWeatherData() {
        val city = cityNameMutable.value ?: return
        val permits = isPermissionAndLocationGranted.value ?: return

        viewModelScope.launch {
            try {
                // Execute the use case to get weather data and update LiveData
                getWeatherDataUseCase.execute(params = city, permits = permits) {
                        weatherModel ->  weatherDataMutable.value = weatherModel
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateCityNameAndPermits(city: String, permits: Boolean) {
        cityNameMutable.value = city
        isPermissionAndLocationGranted.value = permits
        fetchWeatherData()
    }

    fun updatePermits(permits: Boolean){
        isPermissionAndLocationGranted.value = permits
    }

    fun updateSelectedDay(selectedDay: WeatherModel.DailyWeather) {
        selectedWeatherDayMutable.value = selectedDay
    }
}