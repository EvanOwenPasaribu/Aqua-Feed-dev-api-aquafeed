package com.aqua_feed.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aqua_feed.app.R
import com.aqua_feed.app.databinding.ItemLogBinding
import com.aqua_feed.app.models.LogEntryGroup
import com.aquafeed.app.model.response.LogResponseX

class DialogReportAdapter : RecyclerView.Adapter<DialogReportAdapter.ViewHolder>() {

    private lateinit var binding: ItemLogBinding
    private val logs = arrayListOf<LogResponseX>()

    fun setLogs(logData: List<LogEntryGroup>) {
        this.logs.clear()
        this.logs.addAll(logData.map { logItem ->
            LogResponseX(logItem.kgamt.toString(),logItem.date, logItem.schid.toString())
        })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(logs[position], position + 1)
    }

    override fun getItemCount(): Int = logs.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(log: LogResponseX, no: Int) {
            binding.txtFileDate.text = log.fileSize
            binding.txtFileName.text = log.date
            binding.txtFileSize.text = log.fileName

            if (no % 2 == 0) {
                binding.txtFileDate.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
                binding.txtFileName.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
                binding.txtFileSize.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
            } else {
                binding.txtFileDate.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
                binding.txtFileName.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
                binding.txtFileSize.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
            }
        }

    }
}
