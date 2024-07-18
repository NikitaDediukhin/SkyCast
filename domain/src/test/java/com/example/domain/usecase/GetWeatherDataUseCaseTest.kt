package com.example.domain.usecase

import com.example.domain.models.WeatherModel
import com.example.domain.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class GetWeatherDataUseCaseTest {

    @ExperimentalCoroutinesApi
    @Test
    fun `test GetWeatherDataUseCase returns weather data successfully`() = runTest {
        // Arrange: Set up the test repository and use case
        val mockRepository = mock<WeatherRepository>()
        val useCase = GetWeatherDataUseCase(mockRepository)
        val cityName = "London"
        val permits = true

        val expectedWeatherModel = WeatherModel(
            currentWeather = WeatherModel.CurrentWeather(
                city = cityName,
                time = "2024-07-18 15:15",
                avgTemp = 20,
                maxTemp = 25,
                minTemp = 15,
                condition = "Sunny",
                icon = "//cdn.weatherapi.com/weather/64x64/day/113.png"
            ),
            dailyWeather = listOf(
                WeatherModel.DailyWeather(
                    day = "2024-07-18",
                    icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                    avgTemp = 20,
                    maxTemp = 25,
                    minTemp = 15,
                    condition = "Sunny",
                    hourlyWeather = listOf(
                        WeatherModel.HourlyWeather(
                            time = "2024-07-18 01:00",
                            icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                            avgTemp = 18
                        ),
                        WeatherModel.HourlyWeather(
                            time = "2024-07-18 02:00",
                            icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                            avgTemp = 19
                        ),
                        WeatherModel.HourlyWeather(
                            time = "2024-07-18 03:00",
                            icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                            avgTemp = 17
                        )
                    )
                )
            )
        )

        Mockito.`when`(mockRepository.getWeatherData(cityName, permits)).thenReturn(expectedWeatherModel)

        // Act: Execute the use case
        var actualWeatherModel: WeatherModel? = null
        useCase.execute(cityName, permits) { weatherModel ->
            actualWeatherModel = weatherModel
        }

        // Assert: Verify the results
        Assertions.assertNotNull(actualWeatherModel)
        Assertions.assertEquals(cityName, actualWeatherModel?.currentWeather?.city)
        Assertions.assertEquals(expectedWeatherModel.currentWeather.time, actualWeatherModel?.currentWeather?.time)
        Assertions.assertEquals(expectedWeatherModel.currentWeather.avgTemp, actualWeatherModel?.currentWeather?.avgTemp)
        Assertions.assertEquals(expectedWeatherModel.currentWeather.maxTemp, actualWeatherModel?.currentWeather?.maxTemp)
        Assertions.assertEquals(expectedWeatherModel.currentWeather.minTemp, actualWeatherModel?.currentWeather?.minTemp)
        Assertions.assertEquals(expectedWeatherModel.currentWeather.condition, actualWeatherModel?.currentWeather?.condition)
        Assertions.assertEquals(expectedWeatherModel.currentWeather.icon, actualWeatherModel?.currentWeather?.icon)
        Assertions.assertEquals(expectedWeatherModel.dailyWeather.size, actualWeatherModel?.dailyWeather?.size)
    }
}