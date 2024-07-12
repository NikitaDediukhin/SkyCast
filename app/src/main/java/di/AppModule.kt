package di

import android.content.Context
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
    fun providesWeatherViewModelFactory(getWeatherDataUseCase: GetWeatherDataUseCase): WeatherViewModelFactory {
        return WeatherViewModelFactory(getWeatherDataUseCase = getWeatherDataUseCase)
    }
}