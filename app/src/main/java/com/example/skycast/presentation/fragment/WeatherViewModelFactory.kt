package com.example.skycast.presentation.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.usecase.GetWeatherDataUseCase

class WeatherViewModelFactory(
    private val getWeatherDataUseCase: GetWeatherDataUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(
            getWeatherDataUseCase = getWeatherDataUseCase
        ) as T
    }
}