package com.example.skycast.presentation.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.domain.models.WeatherModel
import com.example.skycast.R
import com.example.skycast.databinding.WeatherFragmentBinding
import com.example.skycast.presentation.activity.MainActivity
import com.example.skycast.presentation.adapters.DailyAdapter
import com.example.skycast.presentation.adapters.HourlyAdapter
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

/**
 * Fragment for displaying weather information.
 */
class WeatherFragment: Fragment() {

    private lateinit var binding: WeatherFragmentBinding
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var fLocationClient: FusedLocationProviderClient
    private var weatherFetched: Boolean = false
    private lateinit var weatherViewModel: WeatherViewModel

    private val hourAdapter: HourlyAdapter by lazy { HourlyAdapter() }
    private val dayAdapter: DailyAdapter by lazy { DailyAdapter() }
    private val rvHourWeather: RecyclerView by lazy { requireView().findViewById(R.id.rv_hour) }
    private val rvDayWeather: RecyclerView by lazy { requireView().findViewById(R.id.rv_day) }

    /**
     * Inflates the fragment's view and initializes necessary components.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WeatherFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.shimmerLayout.startShimmer()
        return view
    }

    /**
     * Initializes ViewModel, checks permissions,
     * and sets up observers for weather data updates.
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        weatherViewModel = (activity as MainActivity).vm

        // Check and request location permission
        checkPermission()

        // Initialize UI components and listeners
        init(view)

        // Observe weather data changes
        weatherViewModel.weatherDataLive.observe(viewLifecycleOwner) { weatherModel ->
            showWeatherData(weatherModel)
        }

        // Observe selected weather day changes
        weatherViewModel.selectedWeatherDayLive.observe(viewLifecycleOwner) { selectedDay ->
            updateSelectedDay(selectedDay)
        }

    }

    /**
     * Resumes the fragment. Updates permissions
     * and initiates location fetching if not already fetched.
     */
    override fun onResume() {
        super.onResume()
        updatePermits()
        if(!weatherFetched){
            checkLocation()
            weatherFetched = true
        }
    }

    /**
     * Displays weather data on the UI.
     */
    private fun showWeatherData(weatherModel: WeatherModel?) {

        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE

        activity?.runOnUiThread {
            // Load weather icon using Glide
            binding.ivIcon.let {
                Glide.with(this)
                    .load("https:${weatherModel?.currentWeather?.icon}")
                    .into(it)
            }

            // Set city name and temperature text
            binding.tvCity.text = context?.getString(R.string.current_weather_city, weatherModel?.currentWeather?.city)
            binding.tvTemp.text = context?.getString(R.string.current_weather_temp, weatherModel?.currentWeather?.avgTemp)

            // Set weather condition text with formatted time
            binding.tvCondition.text = context?.getString(
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

            // Update RecyclerView adapters with weather data
            hourAdapter.setHourlyData(weatherModel?.dailyWeather?.get(0)?.hourlyWeather!!)
            dayAdapter.setDailyData(weatherModel.dailyWeather)

            // Scroll RecyclerViews to the top
            rvHourWeather.smoothScrollToPosition(0)
            rvDayWeather.smoothScrollToPosition(0)

        }

    }

    /**
     * Updates UI with selected day's weather data.
     */
    private fun updateSelectedDay(selectedDay: WeatherModel.DailyWeather?) {

        activity?.runOnUiThread {
            // Load selected day's weather icon using Glide
            binding.ivIcon.let {
                Glide.with(this)
                    .load("https:${selectedDay?.icon}")
                    .into(it)
            }

            // Update temperature and weather condition text with formatted time
            binding.tvTemp.text = context?.getString(R.string.current_weather_temp, selectedDay?.avgTemp)
            binding.tvCondition.text = context?.getString(
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

            // Update hourly weather data in RecyclerView
            hourAdapter.setHourlyData(selectedDay?.hourlyWeather!!)

        }

    }

    /**
     * Fetches current device location.
     */
    private fun getLocation() {
        val ct = CancellationTokenSource()
        // Check if location permissions are granted
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
        // Request current location with high accuracy
        fLocationClient
            .getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        // Update city name and permits with the fetched location
                        weatherViewModel.updateCityNameAndPermits("${it.result.latitude},${it.result.longitude}", checkInternetAndLocationIsEnabled())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
    }

    /**
     * Checks and requests location permission.
     */
    private fun checkPermission() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Permission is not granted", Toast.LENGTH_LONG).show()
            }
        }
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        // Request permission if not already granted
        if (!isPermissionGranted(permission)) {
            pLauncher.launch(permission)
        }
    }

    /**
     * Checks if location services are enabled and fetches location if so.
     */
    private fun checkLocation() {
        // If location services are enabled, get the current location
        if(isLocationEnabled()) {
            getLocation()
        } else {
            // If location services are not enabled, show a dialog to prompt the user to enable them
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener{
                override fun onClick(name: String?) {
                    // Open location settings when the user clicks the dialog
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    /**
     * Updates the weatherViewModel with the current status of location and internet permissions.
     */
    private fun updatePermits() {
        val locationEnabled = isLocationEnabled()
        val internetEnabled = isInternetEnabled()
        val permits = locationEnabled && internetEnabled
        weatherViewModel.updatePermits(permits)
    }

    /**
     * Checks if both internet and location services are enabled.
     *
     * @return Boolean indicating if both services are enabled
     */
    private fun checkInternetAndLocationIsEnabled(): Boolean {
        val locationEnabled = isLocationEnabled()
        val internetEnabled = isInternetEnabled()
        return locationEnabled && internetEnabled

    }

    /**
     * Initializes the UI components and sets up event listeners.
     *
     * @param view The fragment's root view
     */
    private fun init(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.button_city_anim)

        // Setting up layout managers for recycler views
        val hourLayoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        val dayLayoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        hourLayoutManager.stackFromEnd = true
        dayLayoutManager.stackFromEnd = true

        // Binding layout managers and adapters to recycler views
        rvHourWeather.layoutManager = hourLayoutManager
        rvHourWeather.adapter = hourAdapter
        rvDayWeather.layoutManager = dayLayoutManager
        rvDayWeather.adapter = dayAdapter

        // Initializing the FusedLocationProviderClient
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Setting up the swipe to refresh layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            try {
                checkLocation()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        // Setting up click listener for dayAdapter
        dayAdapter.setClickListener { selectedDay ->
            weatherViewModel.updateSelectedDay(selectedDay)
        }

        // Setting up click listener for the city button
        binding.btnCity.setOnClickListener {
            binding.btnCity.startAnimation(animation)

            updatePermits()

            DialogManager.searchByCityNameDialog(view.context, object : DialogManager.Listener{
                override fun onClick(name: String?) {
                    if (name.isNullOrEmpty()) {
                        return
                    } else {
                        viewLifecycleOwner.lifecycleScope.launch {
                            try {
                                weatherViewModel.updateCityNameAndPermits(name, checkInternetAndLocationIsEnabled())
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