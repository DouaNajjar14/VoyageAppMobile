package com.example.voyageproject.ui.flight

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.voyageproject.R
import com.example.voyageproject.databinding.FragmentFlightSearchBinding
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class FlightSearchFragment : Fragment() {

    private var _binding: FragmentFlightSearchBinding? = null
    private val binding get() = _binding!!

    private var isRoundTrip = true
    private var departureDate: Calendar? = null
    private var returnDate: Calendar? = null
    private var adultsCount = 1
    private var childrenCount = 0
    private var babiesCount = 0
    private var selectedClass = "Économique"

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlightSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTripTypeSelection()
        setupCitySwap()
        setupDatePickers()
        setupPassengersSelector()
        setupSearchButton()
    }

    private fun setupTripTypeSelection() {
        binding.chipGroupTripType.setOnCheckedStateChangeListener { _, checkedIds ->
            isRoundTrip = checkedIds.contains(R.id.chipRoundTrip)
            binding.tilReturnDate.visibility = if (isRoundTrip) View.VISIBLE else View.GONE
        }
    }

    private fun setupCitySwap() {
        binding.btnSwapCities.setOnClickListener {
            val origin = binding.etOrigin.text.toString()
            val destination = binding.etDestination.text.toString()
            binding.etOrigin.setText(destination)
            binding.etDestination.setText(origin)
        }
    }

    private fun setupDatePickers() {
        binding.etDepartureDate.setOnClickListener {
            showDatePicker { selectedDate ->
                departureDate = selectedDate
                binding.etDepartureDate.setText(dateFormat.format(selectedDate.time))
                
                // Si date de retour déjà sélectionnée et avant la date de départ, la réinitialiser
                if (returnDate != null && returnDate!!.before(selectedDate)) {
                    returnDate = null
                    binding.etReturnDate.setText("Sélectionner une date")
                }
            }
        }

        binding.etReturnDate.setOnClickListener {
            if (departureDate == null) {
                Toast.makeText(context, "Veuillez d'abord sélectionner la date de départ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            showDatePicker(minDate = departureDate) { selectedDate ->
                returnDate = selectedDate
                binding.etReturnDate.setText(dateFormat.format(selectedDate.time))
            }
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
        
        // Définir la date minimale (aujourd'hui ou date de départ)
        datePickerDialog.datePicker.minDate = (minDate ?: today).timeInMillis
        datePickerDialog.show()
    }

    private fun setupPassengersSelector() {
        binding.etPassengers.setOnClickListener {
            val bottomSheet = PassengersBottomSheet(
                adultsCount,
                childrenCount,
                babiesCount,
                selectedClass
            ) { adults, children, babies, flightClass ->
                adultsCount = adults
                childrenCount = children
                babiesCount = babies
                selectedClass = flightClass
                updatePassengersText()
            }
            bottomSheet.show(parentFragmentManager, "PassengersBottomSheet")
        }
    }

    private fun updatePassengersText() {
        val totalPassengers = adultsCount + childrenCount + babiesCount
        val passengersText = buildString {
            if (adultsCount > 0) append("$adultsCount Adulte${if (adultsCount > 1) "s" else ""}")
            if (childrenCount > 0) {
                if (adultsCount > 0) append(", ")
                append("$childrenCount Enfant${if (childrenCount > 1) "s" else ""}")
            }
            if (babiesCount > 0) {
                if (adultsCount > 0 || childrenCount > 0) append(", ")
                append("$babiesCount Bébé${if (babiesCount > 1) "s" else ""}")
            }
            append(", $selectedClass")
        }
        binding.etPassengers.setText(passengersText)
    }

    private fun setupSearchButton() {
        binding.btnSearchFlights.setOnClickListener {
            if (validateSearch()) {
                performSearch()
            }
        }
    }

    private fun validateSearch(): Boolean {
        val origin = binding.etOrigin.text.toString().trim()
        val destination = binding.etDestination.text.toString().trim()

        if (origin.isEmpty()) {
            Toast.makeText(context, "Veuillez saisir la ville de départ", Toast.LENGTH_SHORT).show()
            return false
        }

        if (destination.isEmpty()) {
            Toast.makeText(context, "Veuillez saisir la ville d'arrivée", Toast.LENGTH_SHORT).show()
            return false
        }

        if (origin == destination) {
            Toast.makeText(context, "La ville de départ et d'arrivée doivent être différentes", Toast.LENGTH_SHORT).show()
            return false
        }

        if (departureDate == null) {
            Toast.makeText(context, "Veuillez sélectionner la date de départ", Toast.LENGTH_SHORT).show()
            return false
        }

        if (isRoundTrip && returnDate == null) {
            Toast.makeText(context, "Veuillez sélectionner la date de retour", Toast.LENGTH_SHORT).show()
            return false
        }

        if (adultsCount + childrenCount + babiesCount > 9) {
            Toast.makeText(context, "Maximum 9 passagers autorisés", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun performSearch() {
        val origin = extractCityCode(binding.etOrigin.text.toString())
        val destination = extractCityCode(binding.etDestination.text.toString())

        val intent = Intent(requireContext(), FlightResultsActivity::class.java).apply {
            putExtra("origin", origin)
            putExtra("destination", destination)
            putExtra("departureDate", dateFormat.format(departureDate!!.time))
            putExtra("returnDate", returnDate?.let { dateFormat.format(it.time) })
            putExtra("isRoundTrip", isRoundTrip)
            putExtra("adults", adultsCount)
            putExtra("children", childrenCount)
            putExtra("babies", babiesCount)
            putExtra("class", selectedClass)
        }
        startActivity(intent)
    }

    private fun extractCityCode(cityText: String): String {
        // Extraire le code entre parenthèses, ex: "Paris (PAR)" -> "PAR"
        val regex = "\\(([^)]+)\\)".toRegex()
        val match = regex.find(cityText)
        return match?.groupValues?.get(1) ?: cityText.trim()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
