package com.example.data.mapper

import com.example.data.response.WeatherResponse
import com.example.domain.models.WeatherModel

// Interface for mapping WeatherResponse to WeatherModel
interface WeatherRepositoryMapper {
    /**
     * Converts a WeatherResponse object to a WeatherModel object.
     * @param response The WeatherResponse object received from the API.
     * @return The converted WeatherModel object.
     */
    fun toWeatherModel(response: WeatherResponse): WeatherModel
}

// Implementation of WeatherRepositoryMapper
class WeatherRepositoryMapperImpl: WeatherRepositoryMapper {

    /**
     * Converts a WeatherResponse to a WeatherModel.
     * @param response The WeatherResponse object received from the API.
     * @return The converted WeatherModel object.
     */
    override fun toWeatherModel(response: WeatherResponse): WeatherModel {
        val currentWeather = toCurrentWeather(response) // Converts current weather data
        val dailyWeather = toDailyWeather(response) // Converts daily weather data

        // Creates a WeatherModel object with current and daily weather data
        return WeatherModel(currentWeather = currentWeather, dailyWeather = dailyWeather)
    }

    /**
     * Converts the current weather data from WeatherResponse to WeatherModel.CurrentWeather.
     * @param response The WeatherResponse object received from the API.
     * @return The converted WeatherModel.CurrentWeather object.
     */
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

    /**
     * Converts the daily weather data from WeatherResponse to a list of WeatherModel.DailyWeather.
     * @param response The WeatherResponse object received from the API.
     * @return A list of converted WeatherModel.DailyWeather objects.
     */
    private fun toDailyWeather(response: WeatherResponse): List<WeatherModel.DailyWeather> {
        // Maps each forecast day to a WeatherModel.DailyWeather object
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