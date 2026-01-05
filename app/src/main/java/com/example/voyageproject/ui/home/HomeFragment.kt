package com.example.voyageproject.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.voyageproject.databinding.FragmentHomeNewBinding
import com.example.voyageproject.ui.offers.OffersFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeNewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeNewBinding.inflate(inflater, container, false)

        // Afficher les hôtels par défaut
        showOffers("hotel")

        // Gestion des chips
        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    binding.chipHotels.id -> showOffers("hotel")
                    binding.chipFlights.id -> showOffers("flight")
                    binding.chipCircuits.id -> showOffers("circuit")
                }
            }
        }

        return binding.root
    }

    private fun showOffers(type: String) {
        childFragmentManager.beginTransaction()
            .replace(binding.offersContainer.id, OffersFragment(type))
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
