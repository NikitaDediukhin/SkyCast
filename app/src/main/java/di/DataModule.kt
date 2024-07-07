package di

import android.app.Application
import android.content.Context
import com.example.data.mapper.WeatherRepositoryMapper
import com.example.data.mapper.WeatherRepositoryMapperImpl
import com.example.data.repository.WeatherCache
import com.example.data.repository.WeatherCacheImpl
import com.example.data.repository.WeatherRepositoryImpl
import com.example.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideWeatherCache(context: Context): WeatherCache {
        return WeatherCacheImpl(context)
    }

    @Provides
    @Singleton
    fun provideWeatherRepositoryMapper(): WeatherRepositoryMapper {
        return WeatherRepositoryMapperImpl()
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherRepositoryMapper: WeatherRepositoryMapper,
        weatherCache: WeatherCache
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            weatherCacheImpl = weatherCache,
            weatherRepositoryMapper = weatherRepositoryMapper
        )
    }
}