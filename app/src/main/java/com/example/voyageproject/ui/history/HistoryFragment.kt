package com.example.voyageproject.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voyageproject.ReservationDetailActivity
import com.example.voyageproject.databinding.FragmentHistoryBinding
import com.example.voyageproject.model.Reservation
import com.example.voyageproject.repository.ReservationRepository
import com.example.voyageproject.ui.history.adapters.ReservationAdapter
import com.example.voyageproject.utils.SessionManager

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private val reservationRepo = ReservationRepository()
    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        session = SessionManager(requireContext())

        setupRecyclerView()
        loadHistory()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadHistory() {
        val email = session.getEmail() ?: return

        reservationRepo.getHistory(email).enqueue(object : retrofit2.Callback<List<Reservation>> {
            override fun onResponse(call: retrofit2.Call<List<Reservation>>, response: retrofit2.Response<List<Reservation>>) {
                if (response.isSuccessful) {
                    val reservations = response.body() ?: emptyList()
                    
                    if (reservations.isEmpty()) {
                        binding.tvEmptyHistory.visibility = View.VISIBLE
                        binding.recyclerViewHistory.visibility = View.GONE
                    } else {
                        binding.tvEmptyHistory.visibility = View.GONE
                        binding.recyclerViewHistory.visibility = View.VISIBLE
                        
                        binding.recyclerViewHistory.adapter = ReservationAdapter(reservations) { reservation ->
                            // Ouvrir la page de détails
                            val intent = Intent(requireContext(), ReservationDetailActivity::class.java)
                            intent.putExtra("reservation", reservation)
                            startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Erreur de chargement", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Reservation>>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
