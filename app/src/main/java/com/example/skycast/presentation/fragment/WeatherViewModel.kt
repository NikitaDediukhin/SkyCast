package com.example.skycast.presentation.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.WeatherModel
import com.example.domain.usecase.GetWeatherDataUseCase
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getWeatherDataUseCase: GetWeatherDataUseCase
): ViewModel() {

    private val weatherDataMutable = MutableLiveData<WeatherModel>()
    val weatherDataLive: LiveData<WeatherModel> = weatherDataMutable

    private val selectedWeatherDayMutable = MutableLiveData<WeatherModel.DailyWeather?>()
    val selectedWeatherDayLive: LiveData<WeatherModel.DailyWeather?> = selectedWeatherDayMutable

    private val cityNameMutable = MutableLiveData<String>()

    private val isPermissionAndLocationGranted = MutableLiveData<Boolean>()

    private fun fetchWeatherData() {
        val city = cityNameMutable.value ?: return
        val permits = isPermissionAndLocationGranted.value ?: return

        viewModelScope.launch {
            try {
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