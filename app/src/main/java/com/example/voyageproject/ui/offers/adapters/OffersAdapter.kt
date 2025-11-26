package com.example.voyageproject.ui.offers.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.databinding.ItemOfferBinding
import com.example.voyageproject.model.Circuit
import com.example.voyageproject.model.Flight
import com.example.voyageproject.model.Hotel

class OfferAdapter(
    private val offers: List<Any>,
    private val onClick: (Any) -> Unit
) : RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemOfferBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = offers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer = offers[position]
        when(offer){
            is Hotel -> {
                holder.binding.tvOfferTitle.text = offer.name
                holder.binding.tvOfferDescription.text = "${offer.city}, ${offer.country}"
                holder.binding.tvOfferPrice.text = "Prix moyen: ${offer.rooms.minOfOrNull { it.price } ?: 0} DT"
            }
            is Circuit -> {
                holder.binding.tvOfferTitle.text = offer.title
                holder.binding.tvOfferDescription.text = offer.description
                holder.binding.tvOfferPrice.text = "${offer.prix} DT"
            }
            is Flight -> {
                holder.binding.tvOfferTitle.text = "${offer.origin} -> ${offer.destination}"
                holder.binding.tvOfferDescription.text = "${offer.airline} - Vol: ${offer.flightNumber}"
                holder.binding.tvOfferPrice.text = "${offer.price} DT"
            }
        }

        holder.binding.btnBook.setOnClickListener { onClick(offer) }
    }
}
