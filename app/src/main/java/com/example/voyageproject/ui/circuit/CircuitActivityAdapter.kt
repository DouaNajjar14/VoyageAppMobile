package com.example.voyageproject.ui.circuit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.R
import com.example.voyageproject.model.CircuitActivity
import com.example.voyageproject.model.ChildInfo

class CircuitActivityAdapter(
    private val activities: List<CircuitActivity>,
    private val maxAdults: Int,
    private val children: List<ChildInfo>,
    private val onActivityChanged: () -> Unit
) : RecyclerView.Adapter<CircuitActivityAdapter.ActivityViewHolder>() {

    // Map pour stocker le nombre d'adultes par activité
    private val activityAdultCount = mutableMapOf<String, Int>()
    
    // Map pour stocker le nombre d'enfants par activité
    private val activityChildCount = mutableMapOf<String, Int>()
    
    // Map pour stocker l'état de sélection
    private val selectedActivities = mutableMapOf<String, Boolean>()

    inner class ActivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cbActivity)
        val tvDescription: TextView = view.findViewById(R.id.tvActivityDescription)
        val layoutPersonCount: LinearLayout = view.findViewById(R.id.layoutPersonCount)
        
        // Adultes
        val layoutAdults: LinearLayout = view.findViewById(R.id.layoutAdults)
        val tvAdultCount: TextView = view.findViewById(R.id.tvAdultCount)
        val btnAdultMinus: ImageButton = view.findViewById(R.id.btnAdultMinus)
        val btnAdultPlus: ImageButton = view.findViewById(R.id.btnAdultPlus)
        
        // Enfants
        val layoutChildren: LinearLayout = view.findViewById(R.id.layoutChildren)
        val tvChildCount: TextView = view.findViewById(R.id.tvChildCount)
        val btnChildMinus: ImageButton = view.findViewById(R.id.btnChildMinus)
        val btnChildPlus: ImageButton = view.findViewById(R.id.btnChildPlus)
        
        val tvActivityTotalPrice: TextView = view.findViewById(R.id.tvActivityTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circuit_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        
        // Initialiser les compteurs si pas encore fait
        if (!activityAdultCount.containsKey(activity.id)) {
            activityAdultCount[activity.id] = 0
        }
        if (!activityChildCount.containsKey(activity.id)) {
            activityChildCount[activity.id] = 0
        }
        
        val adultCount = activityAdultCount[activity.id] ?: 0
        val childCount = activityChildCount[activity.id] ?: 0
        val isSelected = selectedActivities[activity.id] ?: false

        // Titre et prix unitaire dans le checkbox
        holder.checkBox.text = "${activity.title} (${String.format("%.0f", activity.price)} TND/adulte)"
        holder.checkBox.textSize = 14f
        holder.checkBox.isChecked = isSelected
        
        // Description
        holder.tvDescription.text = "${activity.description}\n⏱️ Durée: ${activity.duration}"
        
        // Afficher/masquer les sélecteurs
        if (isSelected) {
            holder.layoutPersonCount.visibility = View.VISIBLE
            holder.tvActivityTotalPrice.visibility = View.VISIBLE
            
            // Adultes
            holder.tvAdultCount.text = adultCount.toString()
            
            // Enfants (afficher seulement s'il y a des enfants dans le voyage)
            if (children.isNotEmpty()) {
                holder.layoutChildren.visibility = View.VISIBLE
                holder.tvChildCount.text = childCount.toString()
            } else {
                holder.layoutChildren.visibility = View.GONE
            }
            
            // Calculer et afficher le prix total
            updateTotalPrice(holder, activity, adultCount, childCount)
        } else {
            holder.layoutPersonCount.visibility = View.GONE
            holder.tvActivityTotalPrice.visibility = View.GONE
        }
        
        // Listener checkbox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            selectedActivities[activity.id] = isChecked
            if (isChecked) {
                holder.layoutPersonCount.visibility = View.VISIBLE
                holder.tvActivityTotalPrice.visibility = View.VISIBLE
                
                if (children.isNotEmpty()) {
                    holder.layoutChildren.visibility = View.VISIBLE
                }
                
                updateTotalPrice(holder, activity, adultCount, childCount)
            } else {
                holder.layoutPersonCount.visibility = View.GONE
                holder.tvActivityTotalPrice.visibility = View.GONE
            }
            onActivityChanged()
        }
        
        // Boutons adultes
        holder.btnAdultMinus.setOnClickListener {
            val currentCount = activityAdultCount[activity.id] ?: 0
            if (currentCount > 0) {
                val newCount = currentCount - 1
                activityAdultCount[activity.id] = newCount
                holder.tvAdultCount.text = newCount.toString()
                updateTotalPrice(holder, activity, newCount, childCount)
                onActivityChanged()
            }
        }
        
        holder.btnAdultPlus.setOnClickListener {
            val currentCount = activityAdultCount[activity.id] ?: 0
            if (currentCount < maxAdults) {
                val newCount = currentCount + 1
                activityAdultCount[activity.id] = newCount
                holder.tvAdultCount.text = newCount.toString()
                updateTotalPrice(holder, activity, newCount, childCount)
                onActivityChanged()
            }
        }
        
        // Boutons enfants
        holder.btnChildMinus.setOnClickListener {
            val currentCount = activityChildCount[activity.id] ?: 0
            if (currentCount > 0) {
                val newCount = currentCount - 1
                activityChildCount[activity.id] = newCount
                holder.tvChildCount.text = newCount.toString()
                updateTotalPrice(holder, activity, adultCount, newCount)
                onActivityChanged()
            }
        }
        
        holder.btnChildPlus.setOnClickListener {
            val currentCount = activityChildCount[activity.id] ?: 0
            if (currentCount < children.size) {
                val newCount = currentCount + 1
                activityChildCount[activity.id] = newCount
                holder.tvChildCount.text = newCount.toString()
                updateTotalPrice(holder, activity, adultCount, newCount)
                onActivityChanged()
            }
        }
        
        // Click sur l'item entier
        holder.itemView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
        }
    }
    
    private fun updateTotalPrice(
        holder: ActivityViewHolder,
        activity: CircuitActivity,
        adultCount: Int,
        childCount: Int
    ) {
        // Prix adultes
        val adultPrice = activity.price * adultCount
        
        // Prix enfants (calculé selon l'âge)
        var childPrice = 0.0
        for (i in 0 until childCount.coerceAtMost(children.size)) {
            childPrice += children[i].getActivityPrice(activity.price)
        }
        
        val totalPrice = adultPrice + childPrice
        
        // Afficher le détail
        val priceText = buildString {
            append("Prix: ")
            if (adultCount > 0) {
                append("$adultCount adulte(s) = ${String.format("%.0f", adultPrice)} TND")
            }
            if (childCount > 0) {
                if (adultCount > 0) append(" + ")
                append("$childCount enfant(s) = ${String.format("%.0f", childPrice)} TND")
            }
            append("\nTotal: ${String.format("%.0f", totalPrice)} TND")
        }
        
        holder.tvActivityTotalPrice.text = priceText
    }

    override fun getItemCount() = activities.size
    
    // Méthode pour obtenir les activités sélectionnées avec leur nombre de personnes
    fun getSelectedActivitiesWithCount(): Map<CircuitActivity, Pair<Int, Int>> {
        return activities
            .filter { selectedActivities[it.id] == true }
            .associateWith { 
                Pair(
                    activityAdultCount[it.id] ?: 0,
                    activityChildCount[it.id] ?: 0
                )
            }
    }
    
    // Méthode pour calculer le prix total des activités
    fun getTotalActivitiesPrice(): Double {
        return getSelectedActivitiesWithCount().entries.sumOf { (activity, counts) ->
            val (adultCount, childCount) = counts
            val adultPrice = activity.price * adultCount
            
            var childPrice = 0.0
            for (i in 0 until childCount.coerceAtMost(children.size)) {
                childPrice += children[i].getActivityPrice(activity.price)
            }
            
            adultPrice + childPrice
        }
    }
    
    // Méthode pour calculer le prix des activités (pour adultes seulement)
    fun getTotalActivitiesPriceForAdults(): Double {
        return getSelectedActivitiesWithCount().entries.sumOf { (activity, counts) ->
            val (adultCount, _) = counts
            activity.price * adultCount
        }
    }
}
