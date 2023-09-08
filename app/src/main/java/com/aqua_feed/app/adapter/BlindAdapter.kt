package com.aqua_feed.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aqua_feed.app.R
import com.aqua_feed.app.databinding.ItemBlindCalculatorBinding
import com.aqua_feed.app.models.Blind

class BlindAdapter : RecyclerView.Adapter<BlindAdapter.ViewHolder>() {

    private lateinit var binding: ItemBlindCalculatorBinding
    private val blinds = arrayListOf<Blind>()

    fun setBlinds(blinds: List<Blind>) {
        this.blinds.clear()
        this.blinds.addAll(blinds)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemBlindCalculatorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(blinds[position], position)
    }

    override fun getItemCount(): Int = blinds.size

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItem(blind: Blind, no: Int) {
            binding.txtDoc.text = no.toString()
            binding.txtFD.text = blind.feedPerDay.toString()
            binding.txtTotal.text = blind.total.toString()
            binding.txtBiomass.text = blind.biomass.toString()
            binding.txtMBW.text = blind.mbw.toString()

            if (no % 2 == 0) {
                binding.txtDoc.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
                binding.txtFD.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
                binding.txtTotal.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
                binding.txtBiomass.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
                binding.txtMBW.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_black)
            } else {
                binding.txtDoc.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
                binding.txtFD.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
                binding.txtTotal.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
                binding.txtBiomass.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
                binding.txtMBW.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_table_dark)
            }
        }
    }
}