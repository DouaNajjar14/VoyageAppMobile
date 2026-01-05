package com.example.voyageproject.ui.hotel.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.databinding.ItemAmenityBinding

class AmenityAdapter(
    private val amenities: List<String>
) : RecyclerView.Adapter<AmenityAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAmenityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAmenityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvAmenity.text = amenities[position]
    }

    override fun getItemCount() = amenities.size
}
