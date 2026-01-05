package com.example.voyageproject.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voyageproject.databinding.FragmentNotificationsBinding
import com.example.voyageproject.model.NotificationData
import com.example.voyageproject.repository.NotificationRepository
import com.example.voyageproject.ui.notifications.adapters.NotificationAdapter
import com.example.voyageproject.utils.SessionManager
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    private val notificationRepo = NotificationRepository()
    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        session = SessionManager(requireContext())

        setupRecyclerView()
        loadNotifications()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadNotifications() {
        val email = session.getEmail() ?: return

        lifecycleScope.launch {
            try {
                val response = notificationRepo.getNotifications(email)

                if (response.isSuccessful) {
                    val notifications = response.body() ?: emptyList()
                    
                    if (notifications.isEmpty()) {
                        binding.tvEmptyNotifications.visibility = View.VISIBLE
                        binding.recyclerViewNotifications.visibility = View.GONE
                    } else {
                        binding.tvEmptyNotifications.visibility = View.GONE
                        binding.recyclerViewNotifications.visibility = View.VISIBLE
                        
                        binding.recyclerViewNotifications.adapter = NotificationAdapter(notifications) { notification ->
                            markAsRead(notification)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Erreur de chargement", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun markAsRead(notification: NotificationData) {
        if (notification.isRead) return

        lifecycleScope.launch {
            try {
                val response = notificationRepo.markAsRead(notification.id)

                if (response.isSuccessful) {
                    loadNotifications() // Recharger la liste
                }
            } catch (e: Exception) {
                // Silencieux
            }
        }
    }
}
