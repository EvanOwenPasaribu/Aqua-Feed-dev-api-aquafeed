package com.aqua_feed.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aqua_feed.app.R
import com.aqua_feed.app.databinding.ItemScheduleShortBinding
import com.aqua_feed.app.models.ScheduleEntity

class ScheduleShortAdapter : RecyclerView.Adapter<ScheduleShortAdapter.ViewHolder>() {

    private lateinit var binding: ItemScheduleShortBinding
    private val schedules = arrayListOf<ScheduleEntity>()

    fun setSchedules(schedules: List<ScheduleEntity>) {
        this.schedules.clear()
        this.schedules.addAll(schedules)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemScheduleShortBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(schedules[position], position + 1)
    }

    override fun getItemCount(): Int = schedules.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItem(schedule: ScheduleEntity, no: Int) {
            binding.txtNo.text = no.toString()
            binding.txtTime.text = schedule.time
            binding.txtProgress.text = "${schedule.progress}%"
            binding.txtKg.text = schedule.kg.toString()

            if (no % 2 == 0) {
                binding.txtNo.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
                binding.txtTime.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
                binding.txtProgress.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
                binding.txtKg.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
            } else {
                binding.txtNo.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
                binding.txtTime.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
                binding.txtProgress.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
                binding.txtKg.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
            }
        }
    }
}