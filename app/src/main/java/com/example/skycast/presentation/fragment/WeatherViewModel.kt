package com.example.skycast.presentation.fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mapper.WeatherRepositoryMapper
import com.example.data.mapper.WeatherRepositoryMapperImpl
import com.example.data.repository.WeatherCacheImpl
import com.example.data.repository.WeatherRepositoryImpl
import com.example.domain.models.WeatherModel
import com.example.domain.repository.WeatherRepository
import com.example.domain.usecase.GetWeatherDataUseCase
import com.example.domain.usecase.RefreshWeatherDataUseCase
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val getWeatherDataUseCase: GetWeatherDataUseCase = GetWeatherDataUseCase(weatherRepository = weatherRepository),
    private val refreshWeatherDataUseCase: RefreshWeatherDataUseCase = RefreshWeatherDataUseCase(weatherRepository = weatherRepository)
): ViewModel() {

    private val weatherDataMutable = MutableLiveData<WeatherModel>()
    val weatherDataLive: LiveData<WeatherModel> = weatherDataMutable

    private val selectedWeatherDayMutable = MutableLiveData<WeatherModel.DailyWeather?>()
    val selectedWeatherDayLive: LiveData<WeatherModel.DailyWeather?> = selectedWeatherDayMutable

    private val cityNameMutable = MutableLiveData<String>()

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

    fun updateCityName(city: String) {
        cityNameMutable.value = city
        fetchWeatherData()
    }

    fun updateSelectedDay(selectedDay: WeatherModel.DailyWeather) {
        selectedWeatherDayMutable.value = selectedDay
    }

    fun refreshWeatherData() {
        val city = cityNameMutable.value.toString()

        viewModelScope.launch {
            try {
                refreshWeatherDataUseCase.execute(city) {
                    weatherModel -> weatherDataMutable.value = weatherModel
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}