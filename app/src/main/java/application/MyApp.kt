package application

import android.app.Application
import di.AppComponent
import di.AppModule
import di.DaggerAppComponent
import di.DataModule
import di.DomainModule

class MyApp: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .dataModule(DataModule())
            .domainModule(DomainModule())
            .build()
    }
}