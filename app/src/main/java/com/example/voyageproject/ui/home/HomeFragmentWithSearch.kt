package com.example.voyageproject.ui.home

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.databinding.FragmentHomeWithSearchFormsBinding
import com.example.voyageproject.network.RetrofitClient
import com.example.voyageproject.ui.offers.OffersFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragmentWithSearch : Fragment() {

    private var _binding: FragmentHomeWithSearchFormsBinding? = null
    private val binding get() = _binding!!

    private var currentType = "hotel"
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    // Dates pour circuits
    private var circuitStartDate: Calendar? = null
    private var circuitEndDate: Calendar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeWithSearchFormsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChipSelection()
        setupHotelSearch()
        setupFlightSearch()
        setupCircuitSearch()

        // Afficher les hôtels par défaut
        showAllOffers("hotel")
    }

    private fun setupChipSelection() {
        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    binding.chipHotels.id -> {
                        currentType = "hotel"
                        showSearchForm("hotel")
                    }
                    binding.chipFlights.id -> {
                        currentType = "flight"
                        showSearchForm("flight")
                    }
                    binding.chipCircuits.id -> {
                        currentType = "circuit"
                        showSearchForm("circuit")
                    }
                }
            }
        }
    }

    private fun showSearchForm(type: String) {
        // Masquer tous les formulaires
        binding.cardHotelSearch.visibility = View.GONE
        binding.cardFlightSearch.visibility = View.GONE
        binding.cardCircuitSearch.visibility = View.GONE

        // Afficher le formulaire correspondant
        when (type) {
            "hotel" -> {
                binding.cardHotelSearch.visibility = View.VISIBLE
                showAllOffers("hotel")
            }
            "flight" -> {
                binding.cardFlightSearch.visibility = View.VISIBLE
                showAllOffers("flight")
            }
            "circuit" -> {
                binding.cardCircuitSearch.visibility = View.VISIBLE
                showAllOffers("circuit")
            }
        }
    }

    private fun setupHotelSearch() {
        // Bouton recherche
        binding.btnSearchHotels.setOnClickListener {
            searchHotels()
        }
    }

    private fun setupFlightSearch() {
        binding.btnSearchFlights.setOnClickListener {
            searchFlights()
        }
    }

    private fun setupCircuitSearch() {
        // Bouton recherche - Rechercher directement sans redirection
        binding.btnSearchCircuits.setOnClickListener {
            searchCircuits()
        }
    }

    private fun showDatePicker(minDate: Calendar? = null, onDateSelected: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = (minDate ?: today).timeInMillis
        datePickerDialog.show()
    }

    private fun searchHotels() {
        val destination = binding.etHotelDestination.text.toString().trim()
        val minPrice = binding.etHotelMinPrice.text.toString().toDoubleOrNull()
        val maxPrice = binding.etHotelMaxPrice.text.toString().toDoubleOrNull()

        android.util.Log.d("SEARCH", "=== Recherche Hôtels ===")
        android.util.Log.d("SEARCH", "Destination: $destination")
        android.util.Log.d("SEARCH", "Prix min: $minPrice, Prix max: $maxPrice")

        lifecycleScope.launch {
            try {
                Toast.makeText(context, "Recherche en cours...", Toast.LENGTH_SHORT).show()
                
                val response = RetrofitClient.api.searchHotels(
                    destination = destination.ifEmpty { null },
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    sortBy = "price",
                    sortOrder = "asc"
                )

                android.util.Log.d("SEARCH", "Response code: ${response.code()}")
                android.util.Log.d("SEARCH", "Response successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val hotels = response.body() ?: emptyList()
                    android.util.Log.d("SEARCH", "Nombre d'hôtels: ${hotels.size}")
                    
                    if (hotels.isEmpty()) {
                        Toast.makeText(context, "Aucun hôtel trouvé avec ces critères", Toast.LENGTH_LONG).show()
                        showAllOffers("hotel")
                    } else {
                        showResults(hotels, "hotel")
                        Toast.makeText(context, "${hotels.size} hôtel(s) trouvé(s)", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("SEARCH", "Erreur: $errorBody")
                    Toast.makeText(context, "Erreur de recherche: ${response.code()}", Toast.LENGTH_SHORT).show()
                    showAllOffers("hotel")
                }
            } catch (e: Exception) {
                android.util.Log.e("SEARCH", "Exception: ${e.message}", e)
                Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                showAllOffers("hotel")
            }
        }
    }

    private fun searchFlights() {
        val origin = binding.etFlightOrigin.text.toString().trim()
        val destination = binding.etFlightDestination.text.toString().trim()

        // Rendre les critères optionnels
        android.util.Log.d("SEARCH", "=== Recherche Vols ===")
        android.util.Log.d("SEARCH", "Origine: $origin")
        android.util.Log.d("SEARCH", "Destination: $destination")

        lifecycleScope.launch {
            try {
                Toast.makeText(context, "Recherche en cours...", Toast.LENGTH_SHORT).show()
                
                val response = RetrofitClient.api.searchFlights(
                    origin = origin.ifEmpty { null },
                    destination = destination.ifEmpty { null },
                    minPrice = null,
                    maxPrice = null,
                    sortBy = "price",
                    departureDate = null,
                    totalPassengers = null
                )

                android.util.Log.d("SEARCH", "Nombre de vols: ${response.size}")
                
                if (response.isEmpty()) {
                    Toast.makeText(context, "Aucun vol trouvé avec ces critères", Toast.LENGTH_LONG).show()
                    showAllOffers("flight")
                } else {
                    showResults(response, "flight")
                    Toast.makeText(context, "${response.size} vol(s) trouvé(s)", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("SEARCH", "Exception: ${e.message}", e)
                Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                showAllOffers("flight")
            }
        }
    }

    private fun searchCircuits() {
        val destination = binding.etCircuitDestination.text.toString().trim()
        val duration = binding.etCircuitDuration.text.toString().toIntOrNull()
        val minPrice = binding.etCircuitMinPrice.text.toString().toDoubleOrNull()
        val maxPrice = binding.etCircuitMaxPrice.text.toString().toDoubleOrNull()

        android.util.Log.d("SEARCH", "=== Recherche Circuits ===")
        android.util.Log.d("SEARCH", "Destination: $destination")
        android.util.Log.d("SEARCH", "Durée: $duration jours")
        android.util.Log.d("SEARCH", "Prix min: $minPrice, Prix max: $maxPrice")

        lifecycleScope.launch {
            try {
                Toast.makeText(context, "Recherche en cours...", Toast.LENGTH_SHORT).show()
                
                val response = RetrofitClient.api.searchCircuits(
                    destination = destination.ifEmpty { null },
                    duration = duration,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    sortBy = "price"
                )

                android.util.Log.d("SEARCH", "Response code: ${response.code()}")
                android.util.Log.d("SEARCH", "Response successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val circuits = response.body() ?: emptyList()
                    android.util.Log.d("SEARCH", "Nombre de circuits: ${circuits.size}")
                    
                    if (circuits.isEmpty()) {
                        Toast.makeText(context, "Aucun circuit trouvé avec ces critères", Toast.LENGTH_LONG).show()
                        showAllOffers("circuit")
                    } else {
                        showResults(circuits, "circuit")
                        Toast.makeText(context, "${circuits.size} circuit(s) trouvé(s)", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("SEARCH", "Erreur: $errorBody")
                    Toast.makeText(context, "Erreur de recherche: ${response.code()}", Toast.LENGTH_SHORT).show()
                    showAllOffers("circuit")
                }
            } catch (e: Exception) {
                android.util.Log.e("SEARCH", "Exception: ${e.message}", e)
                Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                showAllOffers("circuit")
            }
        }
    }

    private fun showResults(results: List<Any>, type: String) {
        val fragment = OffersFragment(type, results)
        childFragmentManager.beginTransaction()
            .replace(binding.resultsContainer.id, fragment)
            .commit()
    }

    private fun showAllOffers(type: String) {
        val fragment = OffersFragment(type)
        childFragmentManager.beginTransaction()
            .replace(binding.resultsContainer.id, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
