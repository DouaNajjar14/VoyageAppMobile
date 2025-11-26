package com.example.voyageproject.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.voyageproject.databinding.FragmentHomeBinding
import com.example.voyageproject.ui.offers.OffersFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Afficher les hôtels par défaut
        childFragmentManager.beginTransaction()
            .replace(binding.offersContainer.id, OffersFragment("hotel"))
            .commit()

        // Changer type d'offre selon le bouton cliqué
        binding.btnHotels.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(binding.offersContainer.id, OffersFragment("hotel"))
                .commit()
        }
        binding.btnFlights.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(binding.offersContainer.id, OffersFragment("flight"))
                .commit()
        }
        binding.btnCircuits.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(binding.offersContainer.id, OffersFragment("circuit"))
                .commit()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
