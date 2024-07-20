package di

import android.app.Application
import android.content.Context
import com.example.domain.usecase.GetWeatherDataUseCase
import com.example.skycast.presentation.viewmodel.WeatherViewModelFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {

    @Provides
    @Singleton
    fun providesContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesWeatherViewModelFactory(getWeatherDataUseCase: GetWeatherDataUseCase): WeatherViewModelFactory {
        return WeatherViewModelFactory(getWeatherDataUseCase = getWeatherDataUseCase)
    }
}