package com.example.skycast.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skycast.R
import com.example.domain.models.WeatherModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

class HourlyAdapter: RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    private var hourList: List<WeatherModel.HourlyWeather>? = null

    fun setHourlyData(hourList: List<WeatherModel.HourlyWeather>) {
        this.hourList = hourList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_hour, parent, false)
        return HourlyViewHolder(itemView)
    }

    override fun getItemCount(): Int = hourList?.size ?: 0

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val currentHour = hourList?.get(position)
        if (currentHour != null){
            holder.bind(currentHour)
        }
    }

    class HourlyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tv_item_time)
        private val tvTemp: TextView = itemView.findViewById(R.id.tv_item_temp)
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_item_icon)

        fun bind(hour: WeatherModel.HourlyWeather){
            tvTemp.text = itemView.context.getString(R.string.item_weather_temp, hour.avgTemp)
            ivIcon.let {
                Glide.with(itemView.context)
                    .load("https:${hour.icon}")
                    .into(it)
            }
            tvTime.text = LocalDateTime.parse(
                hour.time,
                DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .append(DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm"))
                    .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    .toFormatter()
            ).format(DateTimeFormatter.ofPattern("HH:mm", Locale("ru"))).toString()
        }

    }


}