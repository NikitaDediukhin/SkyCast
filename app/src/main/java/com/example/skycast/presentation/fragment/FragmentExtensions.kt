@file:Suppress("DEPRECATION")

package com.example.skycast.presentation.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.skycast.R

fun Fragment.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        activity as AppCompatActivity,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.isLocationEnabled(): Boolean {
    val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Fragment.isInternetEnabled(): Boolean {
    val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false

    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

        else -> false
    }
}

object DialogManager {
    fun locationSettingsDialog(context: Context, listener: Listener) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_location_layout, null)
        val builder = AlertDialog.Builder(context, R.style.LocationDialog)
        builder.setView(dialogView)

        val btnSubmit: Button = dialogView.findViewById(R.id.btn_location_dialog_submit)
        val btnCancel: Button = dialogView.findViewById(R.id.btn_location_dialog_cancel)

        val dialog = builder.create()

        dialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.attributes = layoutParams

        dialog.window?.setGravity(Gravity.CENTER)

        layoutParams?.dimAmount = 0.8f
        dialog.window?.attributes = layoutParams

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val animation = AnimationUtils.loadAnimation(context, R.anim.button_city_anim)


        btnSubmit.setOnClickListener {
            btnSubmit.startAnimation(animation)
            listener.onClick(null)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            btnCancel.startAnimation(animation)
            dialog.dismiss()
        }

        dialog.show()
    }
    fun searchByCityNameDialog(context: Context, listener: Listener) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_city_layout, null)
        val builder = AlertDialog.Builder(context, R.style.CityDialog)
        builder.setView(dialogView)

        val btnSubmit: Button = dialogView.findViewById(R.id.btn_city_dialog_submit)
        val btnCancel: Button = dialogView.findViewById(R.id.btn_city_dialog_cancel)
        val etCityName: EditText = dialogView.findViewById(R.id.editText_city_dialog)

        val dialog = builder.create()

        dialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val layoutParams = dialog.window?.attributes
        layoutParams?.width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.attributes = layoutParams

        dialog.window?.setGravity(Gravity.TOP)

        layoutParams?.dimAmount = 0.8f
        dialog.window?.attributes = layoutParams

        dialog.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            statusBarColor = Color.TRANSPARENT
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        val animation = AnimationUtils.loadAnimation(context, R.anim.button_city_anim)

        btnSubmit.setOnClickListener {
            btnSubmit.startAnimation(animation)
            listener.onClick(etCityName.text.toString())
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            btnCancel.startAnimation(animation)
            dialog.dismiss()
        }

        dialog.show()
    }

    interface Listener {
        fun onClick(name: String?)
    }
}

