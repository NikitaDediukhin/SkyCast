package com.example.skycast.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skycast.R
import com.example.domain.models.WeatherModel
import java.text.SimpleDateFormat
import java.util.Locale

class DailyAdapter: RecyclerView.Adapter<DailyAdapter.DailyViewHolder>() {

    private var dayList: List<WeatherModel.DailyWeather>? = null
    private var onDayClickListener: ((WeatherModel.DailyWeather) -> Unit)? = null


    fun setDailyData(dayList: List<WeatherModel.DailyWeather>){
        this.dayList = dayList
        notifyDataSetChanged()
    }

    fun setClickListener(listener: (WeatherModel.DailyWeather) -> Unit) {
        onDayClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DailyViewHolder(itemView)
    }

    override fun getItemCount(): Int = dayList?.size ?: 0

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentDay = dayList?.get(position)
        if (currentDay != null){
            holder.onBind(currentDay)
        }
        holder.itemView.setOnClickListener {
            onDayClickListener?.invoke(currentDay!!)
        }
    }

    class DailyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tv_item_day)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_item_date)
        private val tvTemp: TextView = itemView.findViewById(R.id.tv_item_condition)
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_item_icon)

        fun onBind(forecastDay: WeatherModel.DailyWeather) {
            tvDay.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(forecastDay.day)
                ?.let { SimpleDateFormat("EEE", Locale.getDefault()).format(it).uppercase() }
            tvDate.text =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(forecastDay.day)
                    ?.let { SimpleDateFormat("dd/MM", Locale.getDefault()).format(it) }

            tvTemp.text = forecastDay.condition
            ivIcon.let {
                Glide.with(itemView.context)
                    .load("https:${forecastDay.icon}")
                    .into(it)
            }
        }

    }
}