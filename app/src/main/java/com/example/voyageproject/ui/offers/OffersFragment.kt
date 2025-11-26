package com.example.voyageproject.ui.offers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.databinding.FragmentOffersBinding
import com.example.voyageproject.model.Circuit
import com.example.voyageproject.model.Flight
import com.example.voyageproject.model.Hotel
import com.example.voyageproject.network.RetrofitClient
import com.example.voyageproject.ui.offers.adapters.OfferAdapter
import com.example.voyageproject.utils.SessionManager
import kotlinx.coroutines.launch
import kotlin.getValue

class OffersFragment(private val type: String) : Fragment() {

    private lateinit var binding: FragmentOffersBinding
    private val api by lazy { RetrofitClient.api }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOffersBinding.inflate(inflater, container, false)
        loadOffers()
        return binding.root
    }

    private fun loadOffers() {
        lifecycleScope.launch {
            val response = when(type){
                "hotel" -> api.getHotels()
                "circuit" -> api.getCircuits()
                "flight" -> api.getFlights()
                else -> null
            }

            Log.d("OFFERS_DEBUG", "Response: $response")

            if(response != null){
                Log.d("OFFERS_DEBUG", "Code: ${response.code()}")
                Log.d("OFFERS_DEBUG", "Body: ${response.body()}")
            }

            if(response != null && response.isSuccessful){
                val offers = response.body()!!
                binding.recyclerViewOffers.adapter = OfferAdapter(offers) { offer ->
                    bookOffer(offer)
                }
            } else {
                Toast.makeText(requireContext(), "Erreur chargement offres", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun bookOffer(offer: Any){
        val email = SessionManager(requireContext()).getEmail() ?: return
        val body = when(offer){
            is Hotel -> mapOf("email" to email, "hotelId" to offer.id)
            is Circuit -> mapOf("email" to email, "circuitId" to offer.id)
            is Flight -> mapOf("email" to email, "flightId" to offer.id)
            else -> emptyMap()
        }
        lifecycleScope.launch {
            val res = api.bookOffer(body)
            if(res.isSuccessful){
                Toast.makeText(requireContext(), "Réservation réussie", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Erreur réservation", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
