package com.example.skycast.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import androidx.lifecycle.ViewModelProvider
import application.MyApp
import com.example.skycast.R
import com.example.skycast.presentation.fragment.WeatherFragment
import com.example.skycast.presentation.fragment.WeatherViewModel
import com.example.skycast.presentation.fragment.WeatherViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var vmFactory: WeatherViewModelFactory

    lateinit var vm: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        (applicationContext as MyApp).appComponent.inject(this)

        vm = ViewModelProvider(this, vmFactory).get(WeatherViewModel::class.java)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, WeatherFragment())
            .commit()

    }
}