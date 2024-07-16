package com.example.skycast.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import application.MyApp
import com.example.skycast.R
import com.example.skycast.presentation.fragment.WeatherFragment
import com.example.skycast.presentation.fragment.WeatherViewModel
import com.example.skycast.presentation.fragment.WeatherViewModelFactory
import javax.inject.Inject

/**
 * MainActivity serves as the entry point for the SkyCast application.
 * It initializes the ViewModel and displays the WeatherFragment.
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var vmFactory: WeatherViewModelFactory // Factory for creating WeatherViewModel instances

    lateinit var vm: WeatherViewModel // Instance of WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inject dependencies using Dagger
        (applicationContext as MyApp).appComponent.inject(this)

        // Initialize the ViewModel
        vm = ViewModelProvider(this, vmFactory).get(WeatherViewModel::class.java)

        // Add WeatherFragment to the activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, WeatherFragment())
            .commit()

    }
}