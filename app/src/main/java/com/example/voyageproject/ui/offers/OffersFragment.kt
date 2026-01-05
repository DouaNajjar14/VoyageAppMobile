package com.example.voyageproject.ui.offers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voyageproject.databinding.FragmentOffersNewBinding
import com.example.voyageproject.network.RetrofitClient
import com.example.voyageproject.ui.offers.adapters.PremiumOffersAdapter
import com.example.voyageproject.utils.SessionManager
import kotlinx.coroutines.launch

class OffersFragment(
    private val type: String,
    private val filteredResults: List<Any>? = null
) : Fragment() {

    private lateinit var binding: FragmentOffersNewBinding
    private val api by lazy { RetrofitClient.api }
    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?, 
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOffersNewBinding.inflate(inflater, container, false)
        session = SessionManager(requireContext())
        
        setupRecyclerView()
        
        // Vérifier si l'utilisateur est connecté
        if (!session.isLoggedIn()) {
            showLoginRequired()
        } else {
            // Si des résultats filtrés sont fournis, les afficher directement
            if (filteredResults != null) {
                showFilteredResults(filteredResults)
            } else {
                loadOffers()
            }
        }
        
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewOffers.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showLoginRequired() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewOffers.visibility = View.GONE
        binding.tvEmptyState.visibility = View.VISIBLE
        binding.tvEmptyState.text = "⚠️ Connexion requise\n\nVeuillez vous connecter pour voir les ${
            when(type) {
                "hotel" -> "hôtels"
                "flight" -> "vols"
                "circuit" -> "circuits"
                else -> "offres"
            }
        }"
        
        Log.w("OFFERS", "❌ Utilisateur non connecté")
        Toast.makeText(requireContext(), "Veuillez vous connecter", Toast.LENGTH_LONG).show()
    }

    private fun loadOffers() {
        val email = session.getEmail()
        Log.d("OFFERS", "========================================")
        Log.d("OFFERS", "=== CHARGEMENT $type ===")
        Log.d("OFFERS", "Email utilisateur: $email")
        Log.d("OFFERS", "========================================")
        
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewOffers.visibility = View.GONE
        binding.tvEmptyState.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = when(type) {
                    "hotel" -> {
                        Log.d("OFFERS", "📞 GET /api/hotels")
                        api.getHotels()
                    }
                    "circuit" -> {
                        Log.d("OFFERS", "📞 GET /api/circuits")
                        Log.d("OFFERS", "⚠️ ATTENTION: Vérifiez CIRCUIT_DESER dans Logcat")
                        api.getCircuits()
                    }
                    "flight" -> {
                        Log.d("OFFERS", "📞 GET /api/flights")
                        api.getFlights()
                    }
                    else -> null
                }

                Log.d("OFFERS", "✅ Réponse reçue")
                Log.d("OFFERS", "Code: ${response?.code()}")
                Log.d("OFFERS", "Success: ${response?.isSuccessful}")
                
                if (response != null && !response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("OFFERS", "❌ Error body: $errorBody")
                }

                binding.progressBar.visibility = View.GONE

                if(response != null && response.isSuccessful) {
                    val offers = response.body() ?: emptyList()
                    
                    Log.d("OFFERS", "📊 Nombre: ${offers.size}")
                    
                    if (offers.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.tvEmptyState.text = when(type) {
                            "hotel" -> "Aucun hôtel disponible"
                            "flight" -> "Aucun vol disponible"
                            "circuit" -> "Aucun circuit disponible"
                            else -> "Aucune offre disponible"
                        }
                        Log.w("OFFERS", "⚠️ Liste vide")
                    } else {
                        binding.recyclerViewOffers.visibility = View.VISIBLE
                        binding.recyclerViewOffers.adapter = PremiumOffersAdapter(offers, type)
                        Log.d("OFFERS", "✅ ${offers.size} offres affichées")
                    }
                } else {
                    val errorBody = response?.errorBody()?.string()
                    Log.e("OFFERS", "❌ Erreur ${response?.code()}")
                    Log.e("OFFERS", "❌ Message: ${response?.message()}")
                    Log.e("OFFERS", "❌ Body: $errorBody")
                    
                    binding.tvEmptyState.visibility = View.VISIBLE
                    
                    if (response?.code() == 401) {
                        binding.tvEmptyState.text = "⚠️ Erreur d'authentification (401)\n\n" +
                            "Le backend refuse votre email.\n\n" +
                            "Email utilisé: $email\n\n" +
                            "Essayez de vous déconnecter et reconnecter."
                        
                        Toast.makeText(
                            requireContext(),
                            "Erreur 401: Reconnectez-vous",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        binding.tvEmptyState.text = "Erreur ${response?.code()}\n${response?.message()}"
                        Toast.makeText(
                            requireContext(), 
                            "Erreur: ${response?.code()}", 
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("OFFERS", "========================================")
                Log.e("OFFERS", "❌ EXCEPTION")
                Log.e("OFFERS", "Type: ${e.javaClass.simpleName}")
                Log.e("OFFERS", "Message: ${e.message}")
                Log.e("OFFERS", "========================================", e)
                
                binding.progressBar.visibility = View.GONE
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.tvEmptyState.text = "Erreur: ${e.message}"
                
                Toast.makeText(
                    requireContext(), 
                    "Erreur: ${e.message}", 
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showFilteredResults(results: List<Any>) {
        binding.progressBar.visibility = View.GONE
        
        if (results.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.tvEmptyState.text = when(type) {
                "hotel" -> "Aucun hôtel trouvé"
                "flight" -> "Aucun vol trouvé"
                "circuit" -> "Aucun circuit trouvé"
                else -> "Aucun résultat"
            }
        } else {
            binding.recyclerViewOffers.visibility = View.VISIBLE
            binding.recyclerViewOffers.adapter = PremiumOffersAdapter(results, type)
        }
    }
}
