package com.example.voyageproject.ui.hotel.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ItemRoomOptionBinding
import com.example.voyageproject.model.Child
import com.example.voyageproject.model.MealOption
import com.example.voyageproject.model.RoomOption
import com.example.voyageproject.model.ViewOption

class RoomOptionAdapter(
    private val numberOfNights: Int,
    private val adultsCount: Int = 2,
    private val children: List<Child> = emptyList(),
    private val onRoomSelected: (RoomOption, ViewOption, MealOption, Double) -> Unit
) : ListAdapter<RoomOption, RoomOptionAdapter.RoomViewHolder>(RoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RoomViewHolder(
        private val binding: ItemRoomOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentRoom: RoomOption? = null
        private var selectedView: ViewOption? = null
        private var selectedMeal: MealOption? = null

        fun bind(room: RoomOption) {
            currentRoom = room

            // Room info
            binding.tvRoomType.text = "🛏️ ${room.roomType}"
            binding.tvRoomCapacity.text = "👥 ${room.capacity} personne${if (room.capacity > 1) "s" else ""}"
            binding.tvBedType.text = "🛏️ ${room.bedType}"

            // Setup view options
            setupViewOptions(room.viewOptions)

            // Setup meal options
            setupMealOptions(room.mealOptions)

            // Initial price calculation
            selectedView = room.viewOptions.firstOrNull()
            selectedMeal = room.mealOptions.firstOrNull()
            updatePrice()

            // Select button
            binding.btnSelectRoom.setOnClickListener {
                val view = selectedView ?: room.viewOptions.first()
                val meal = selectedMeal ?: room.mealOptions.first()
                val totalPrice = calculateTotalPrice(room, view, meal)
                onRoomSelected(room, view, meal, totalPrice)
            }
        }

        private fun setupViewOptions(viewOptions: List<ViewOption>) {
            binding.rgViewOptions.removeAllViews()

            viewOptions.forEachIndexed { index, viewOption ->
                val radioButton = RadioButton(binding.root.context).apply {
                    id = ViewGroup.generateViewId()
                    text = getViewLabel(viewOption)
                    textSize = 14f
                    setPadding(8, 8, 8, 8)
                    
                    setOnClickListener {
                        selectedView = viewOption
                        updatePrice()
                    }
                }

                binding.rgViewOptions.addView(radioButton)

                if (index == 0) {
                    radioButton.isChecked = true
                    selectedView = viewOption
                }
            }
        }

        private fun setupMealOptions(mealOptions: List<MealOption>) {
            binding.rgMealOptions.removeAllViews()

            mealOptions.forEachIndexed { index, mealOption ->
                val radioButton = RadioButton(binding.root.context).apply {
                    id = ViewGroup.generateViewId()
                    text = getMealLabel(mealOption)
                    textSize = 14f
                    setPadding(8, 8, 8, 8)
                    
                    setOnClickListener {
                        selectedMeal = mealOption
                        updatePrice()
                    }
                }

                binding.rgMealOptions.addView(radioButton)

                if (index == 0) {
                    radioButton.isChecked = true
                    selectedMeal = mealOption
                }
            }
        }

        private fun getViewLabel(viewOption: ViewOption): String {
            val icon = when (viewOption.type) {
                "garden" -> "🌳"
                "sea" -> "🌊"
                "pool" -> "🏊"
                else -> "🏨"
            }
            val price = if (viewOption.pricePerNight > 0) {
                " (+${viewOption.pricePerNight.toInt()} TND/nuit)"
            } else {
                " (+0 TND)"
            }
            return "$icon ${viewOption.label}$price"
        }

        private fun getMealLabel(mealOption: MealOption): String {
            val icon = when (mealOption.type) {
                "breakfast" -> "🍳"
                "half_board" -> "🍽️"
                "all_inclusive" -> "🍹"
                else -> ""
            }
            val price = if (mealOption.pricePerNight > 0) {
                " (+${mealOption.pricePerNight.toInt()} TND/nuit)"
            } else {
                " (+0 TND)"
            }
            return if (icon.isNotEmpty()) {
                "$icon ${mealOption.label}$price"
            } else {
                "${mealOption.label}$price"
            }
        }

        private fun updatePrice() {
            val room = currentRoom ?: return
            val view = selectedView ?: return
            val meal = selectedMeal ?: return

            val totalPrice = calculateTotalPrice(room, view, meal)
            val pricePerNight = room.basePrice + view.pricePerNight + meal.pricePerNight

            binding.tvTotalPrice.text = "${totalPrice.toInt()} TND"
            binding.tvPricePerNight.text = "${pricePerNight.toInt()} TND/nuit"
        }

        private fun calculateTotalPrice(room: RoomOption, view: ViewOption, meal: MealOption): Double {
            // Prix par nuit
            val pricePerNight = room.basePrice + view.pricePerNight + meal.pricePerNight
            
            // Calcul avec adultes et enfants
            // Adultes: prix complet
            var totalPrice = pricePerNight * numberOfNights * adultsCount
            
            // Enfants: gratuit si <12 ans, 50% si ≥12 ans
            for (child in children) {
                if (child.age >= 12) {
                    totalPrice += pricePerNight * numberOfNights * 0.5
                }
                // Si age < 12, c'est gratuit, donc on n'ajoute rien
            }
            
            return totalPrice
        }
    }

    private class RoomDiffCallback : DiffUtil.ItemCallback<RoomOption>() {
        override fun areItemsTheSame(oldItem: RoomOption, newItem: RoomOption): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RoomOption, newItem: RoomOption): Boolean {
            return oldItem == newItem
        }
    }
}
