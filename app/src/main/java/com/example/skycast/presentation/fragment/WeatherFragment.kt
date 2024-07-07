package com.example.skycast.presentation.fragment

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.domain.models.WeatherModel
import com.example.domain.usecase.GetWeatherDataUseCase
import com.example.skycast.R
import com.example.skycast.presentation.activity.MainActivity
import com.example.skycast.presentation.adapters.DailyAdapter
import com.example.skycast.presentation.adapters.HourlyAdapter
import di.AppContainer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

class WeatherFragment: Fragment() {

    private lateinit var hourAdapter: HourlyAdapter
    private lateinit var dayAdapter: DailyAdapter
    private lateinit var rvHourWeather: RecyclerView
    private lateinit var rvDayWeather: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tvCity: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvCondition: TextView
    private lateinit var ivIcon: ImageView
    private lateinit var btnCity: ImageButton

    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var fLocationClient: FusedLocationProviderClient
    private var weatherFetched: Boolean = false

    private lateinit var shimmerLayout: ShimmerFrameLayout

    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.weather_fragment, container, false)
        shimmerLayout = view.findViewById(R.id.shimmerLayout)
        shimmerLayout.startShimmer()
        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel = (activity as MainActivity).vm

        checkPermission()
        init(view)

        weatherViewModel.weatherDataLive.observe(viewLifecycleOwner) { weatherModel ->
            showWeatherData(weatherModel)
        }
        weatherViewModel.selectedWeatherDayLive.observe(viewLifecycleOwner) { selectedDay ->
            updateSelectedDay(selectedDay)
        }

    }

    override fun onResume() {
        super.onResume()
        updatePermits()
        if(!weatherFetched){
            checkLocation()
            weatherFetched = true
        }
    }

    private fun showWeatherData(weatherModel: WeatherModel?) {

        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE

        requireActivity().runOnUiThread {
            ivIcon.let {
                Glide.with(this)
                    .load("https:${weatherModel?.currentWeather?.icon}")
                    .into(it)
            }

            tvCity.text = context?.getString(R.string.current_weather_city, weatherModel?.currentWeather?.city)
            tvTemp.text = context?.getString(R.string.current_weather_temp, weatherModel?.currentWeather?.avgTemp)
            tvCondition.text = context?.getString(
                R.string.current_weather_condition,
                weatherModel?.currentWeather?.condition,
                weatherModel?.currentWeather?.maxTemp,
                weatherModel?.currentWeather?.minTemp,
                LocalDateTime.parse(
                    weatherModel?.currentWeather?.time,
                    DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .append(DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm"))
                        .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        .toFormatter()
                ).format(DateTimeFormatter.ofPattern("EEE, HH:mm", Locale("ru"))).toString()
            )

            hourAdapter.setHourlyData(weatherModel?.dailyWeather?.get(0)?.hourlyWeather!!)
            dayAdapter.setDailyData(weatherModel.dailyWeather)

            rvHourWeather.smoothScrollToPosition(0)
            rvDayWeather.smoothScrollToPosition(0)

        }

    }

    private fun updateSelectedDay(selectedDay: WeatherModel.DailyWeather?) {

        requireActivity().runOnUiThread {
            ivIcon.let {
                Glide.with(this)
                    .load("https:${selectedDay?.icon}")
                    .into(it)
            }

            tvTemp.text = context?.getString(R.string.current_weather_temp, selectedDay?.avgTemp)
            tvCondition.text = context?.getString(
                R.string.current_weather_condition,
                selectedDay?.condition,
                selectedDay?.maxTemp,
                selectedDay?.minTemp,
                LocalDate.parse(
                    selectedDay?.day,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ).atStartOfDay().format(
                    DateTimeFormatter.ofPattern("EEE, HH:mm", Locale("ru"))
                )
            )

            hourAdapter.setHourlyData(selectedDay?.hourlyWeather!!)

        }

    }

    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        weatherViewModel.updateCityNameAndPermits("${it.result.latitude},${it.result.longitude}", checkInternetAndLocation())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
        }
    }

    private fun checkPermission() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Разрешение не получено", Toast.LENGTH_LONG).show()
            }
        }
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (!isPermissionGranted(permission)) {
            pLauncher.launch(permission)
        }
    }

    private fun checkLocation() {
        if(isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener{
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun updatePermits() {
        val locationEnabled = isLocationEnabled()
        val internetEnabled = isInternetEnabled()

        val permits = locationEnabled && internetEnabled

        weatherViewModel.updatePermits(permits)
    }

    private fun checkInternetAndLocation(): Boolean {
        val locationEnabled = isLocationEnabled()
        val internetEnabled = isInternetEnabled()

        return locationEnabled && internetEnabled

    }

    private fun init(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        tvCity = view.findViewById(R.id.tv_city)
        tvTemp = view.findViewById(R.id.tv_temp)
        tvCondition = view.findViewById(R.id.tv_condition)
        ivIcon = view.findViewById(R.id.iv_icon)

        btnCity = view.findViewById(R.id.btn_city)
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.button_city_anim)

        rvHourWeather = view.findViewById(R.id.rv_hour)
        rvDayWeather = view.findViewById(R.id.rv_day)
        hourAdapter = HourlyAdapter()
        dayAdapter = DailyAdapter()

        val hourLayoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        val dayLayoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)

        hourLayoutManager.stackFromEnd = true
        dayLayoutManager.stackFromEnd = true

        rvHourWeather.layoutManager = hourLayoutManager
        rvHourWeather.adapter = hourAdapter

        rvDayWeather.layoutManager = dayLayoutManager
        rvDayWeather.adapter = dayAdapter

        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        swipeRefreshLayout.setOnRefreshListener {
            try {
                checkLocation()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                swipeRefreshLayout.isRefreshing = false
            }
        }
        dayAdapter.setClickListener { selectedDay ->
            weatherViewModel.updateSelectedDay(selectedDay)
        }
        btnCity.setOnClickListener {
            btnCity.startAnimation(animation)
            updatePermits()
            DialogManager.searchByCityNameDialog(view.context, object : DialogManager.Listener{
                override fun onClick(name: String?) {
                    if (name.isNullOrEmpty()) {
                        return
                    } else {
                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                weatherViewModel.updateCityNameAndPermits(name, checkInternetAndLocation())
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {

                            }
                        }
                    }
                }

            })
        }
    }
}