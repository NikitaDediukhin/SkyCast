package di

import android.app.Application
import android.content.Context
import com.example.data.mapper.WeatherRepositoryMapper
import com.example.data.repository.WeatherCache
import com.example.domain.repository.WeatherRepository
import com.example.domain.usecase.GetWeatherDataUseCase
import com.example.skycast.presentation.fragment.WeatherViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun providesContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun providesWeatherViewModelFactory(
        getWeatherDataUseCase: GetWeatherDataUseCase,
        weatherRepositoryMapper: WeatherRepositoryMapper,
        weatherCache: WeatherCache,
        weatherRepository: WeatherRepository
    ): WeatherViewModelFactory {
        return WeatherViewModelFactory(
            getWeatherDataUseCase = getWeatherDataUseCase,
            weatherRepositoryMapper = weatherRepositoryMapper,
            weatherCache = weatherCache,
            weatherRepository = weatherRepository
        )
    }
}