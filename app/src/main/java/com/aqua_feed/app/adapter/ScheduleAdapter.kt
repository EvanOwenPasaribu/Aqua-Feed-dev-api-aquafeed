package com.aqua_feed.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aqua_feed.app.R
import com.aqua_feed.app.databinding.ItemScheduleBinding
import com.aqua_feed.app.models.ScheduleEntity


class ScheduleAdapter(
    private val onEdit: (ScheduleEntity, Int) -> Unit,
    private val onChecked: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private val schedules = arrayListOf<ScheduleEntity>()

    fun setSchedules(schedules: List<ScheduleEntity>) {
        this.schedules.clear()
        this.schedules.addAll(schedules)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(schedules[position], position)
    }

    override fun getItemCount(): Int = schedules.size

    inner class ViewHolder(private val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(schedule: ScheduleEntity, no: Int) {
            binding.txtNo.text = no.toString()
            binding.txtTime.text = schedule.time
            binding.txtDuration.text = schedule.duration.toString()
            binding.txtKg.text = schedule.kg.toString()
            binding.switchStatus.isChecked = schedule.status

            binding.root.setOnClickListener {
                onEdit(schedule, no)
            }

            binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
                onChecked(schedule.id, isChecked)
            }

            val isEven = no % 2 == 0
            val backgroundRes = if (isEven) R.drawable.background_table_black else R.drawable.background_table_dark
            binding.txtNo.background = ContextCompat.getDrawable(itemView.context, backgroundRes)
            binding.txtTime.background = ContextCompat.getDrawable(itemView.context, backgroundRes)
            binding.txtDuration.background = ContextCompat.getDrawable(itemView.context, backgroundRes)
            binding.txtKg.background = ContextCompat.getDrawable(itemView.context, backgroundRes)
            binding.layoutStatus.background = ContextCompat.getDrawable(itemView.context, backgroundRes)
        }
    }
}
