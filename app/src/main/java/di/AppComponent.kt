package di

import com.example.data.repository.WeatherCacheImpl
import com.example.skycast.presentation.activity.MainActivity
import com.example.skycast.presentation.fragment.WeatherFragment
import com.example.skycast.presentation.fragment.WeatherViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataModule::class, DomainModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(weatherCacheImpl: WeatherCacheImpl)
}