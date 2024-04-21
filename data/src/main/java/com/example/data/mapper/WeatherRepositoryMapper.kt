package com.example.data.mapper

import com.example.data.response.WeatherResponse
import com.example.domain.models.WeatherModel

interface WeatherRepositoryMapper {
    fun toWeatherModel(response: WeatherResponse): WeatherModel
}

class WeatherRepositoryMapperImpl: WeatherRepositoryMapper {

    override fun toWeatherModel(response: WeatherResponse): WeatherModel {
        val currentWeather = toCurrentWeather(response)
        val dailyWeather = toDailyWeather(response)

        return WeatherModel(currentWeather = currentWeather, dailyWeather = dailyWeather)
    }

    private fun toCurrentWeather(response: WeatherResponse): WeatherModel.CurrentWeather {
        return WeatherModel.CurrentWeather(
            city = response.location.name,
            time = response.location.localtime,
            avgTemp = response.forecast.forecastday[0].dayWeather.avgTemp.toInt(),
            maxTemp = response.forecast.forecastday[0].dayWeather.maxTemp.toInt(),
            minTemp = response.forecast.forecastday[0].dayWeather.minTemp.toInt(),
            condition = response.forecast.forecastday[0].dayWeather.condition.text,
            icon = response.forecast.forecastday[0].dayWeather.condition.icon
        )
    }

    private fun toDailyWeather(response: WeatherResponse): List<WeatherModel.DailyWeather> {
        return response.forecast.forecastday.map { forecastDay ->
            WeatherModel.DailyWeather(
                day = forecastDay.date,
                icon = forecastDay.dayWeather.condition.icon,
                avgTemp = forecastDay.dayWeather.avgTemp.toInt(),
                maxTemp = forecastDay.dayWeather.maxTemp.toInt(),
                minTemp = forecastDay.dayWeather.minTemp.toInt(),
                condition = forecastDay.dayWeather.condition.text,
                hourlyWeather = forecastDay.hourWeather.map { item ->
                    WeatherModel.HourlyWeather(
                        time = item.time,
                        icon = item.condition.icon,
                        avgTemp = item.avgTemp.toInt()
                    )
                }
            )
        }
    }
}