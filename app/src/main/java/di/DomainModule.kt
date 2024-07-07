package di

import com.example.domain.repository.WeatherRepository
import com.example.domain.usecase.GetWeatherDataUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule {

    @Provides
    @Singleton
    fun provideGetWeatherDataUseCase(weatherRepository: WeatherRepository): GetWeatherDataUseCase{
        return GetWeatherDataUseCase(weatherRepository = weatherRepository)
    }
}