package com.example.voyageproject.ui.notifications.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.databinding.ItemNotificationBinding
import com.example.voyageproject.model.NotificationData

class NotificationAdapter(
    private val notifications: List<NotificationData>,
    private val onNotificationClick: (NotificationData) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationData) {
            binding.tvTitle.text = notification.title
            binding.tvMessage.text = notification.message
            binding.tvTimestamp.text = notification.timestamp

            // Indicateur non lu
            binding.viewUnreadIndicator.visibility = if (notification.isRead) {
                View.GONE
            } else {
                View.VISIBLE
            }

            binding.root.setOnClickListener {
                onNotificationClick(notification)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size
}
