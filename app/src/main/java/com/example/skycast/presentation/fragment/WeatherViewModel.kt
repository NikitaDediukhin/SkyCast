package com.example.skycast.presentation.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mapper.WeatherRepositoryMapper
import com.example.data.mapper.WeatherRepositoryMapperImpl
import com.example.data.repository.WeatherRepositoryImpl
import com.example.domain.models.WeatherModel
import com.example.domain.repository.WeatherRepository
import com.example.domain.usecase.GetWeatherDataUseCase
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherRepositoryMapper: WeatherRepositoryMapper = WeatherRepositoryMapperImpl(),
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl(weatherRepositoryMapper = weatherRepositoryMapper),
    private val getWeatherDataUseCase: GetWeatherDataUseCase = GetWeatherDataUseCase(weatherRepository = weatherRepository)
): ViewModel() {

    private val weatherDataMutable = MutableLiveData<WeatherModel>()
    val weatherDataLive: LiveData<WeatherModel> = weatherDataMutable

    private val selectedWeatherDayMutable = MutableLiveData<WeatherModel.DailyWeather?>()
    val selectedWeatherDayLive: LiveData<WeatherModel.DailyWeather?> = selectedWeatherDayMutable

    private val cityNameMutable = MutableLiveData<String>()
    val cityNameLive: LiveData<String> = cityNameMutable

    fun updateCityName(city: String) {
        cityNameMutable.value = city
        fetchWeatherData()
    }

    fun updateSelectedDay(selectedDay: WeatherModel.DailyWeather) {
        selectedWeatherDayMutable.value = selectedDay
    }

    private fun fetchWeatherData() {
        val city = cityNameMutable.value ?: return

        viewModelScope.launch {
            try {
                getWeatherDataUseCase.execute(city) {
                        weatherModel ->  weatherDataMutable.value = weatherModel
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}