package com.example.skycast.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import com.example.skycast.R
import com.example.skycast.presentation.fragment.WeatherFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, WeatherFragment())
            .commit()

    }







}