package com.example.voyageproject.ui.hotel

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ActivityHotelDetailsBookingBinding
import com.example.voyageproject.databinding.DialogChildAgeBinding
import com.example.voyageproject.model.Child
import com.example.voyageproject.model.Hotel
import java.text.SimpleDateFormat
import java.util.*

class HotelDetailsBookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelDetailsBookingBinding
    private var hotel: Hotel? = null
    
    private var checkInDate: Calendar = Calendar.getInstance()
    private var checkOutDate: Calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
    
    private var adultsCount = 2
    private val children = mutableListOf<Child>()
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelDetailsBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        getHotelData()
        setupDatePickers()
        setupGuestCounters()
        setupViewRoomsButton()
        updateUI()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish()  }
    }

    private fun getHotelData() {
        // Récupérer l'hôtel depuis l'intent
        hotel = intent.getParcelableExtra("hotel")
        
        Log.d("HOTEL_BOOKING", "========================================")
        Log.d("HOTEL_BOOKING", "Hotel reçu dans HotelDetailsBookingActivity:")
        Log.d("HOTEL_BOOKING", "  ID: ${hotel?.id}")
        Log.d("HOTEL_BOOKING", "  Nom: ${hotel?.name}")
        Log.d("HOTEL_BOOKING", "  Ville: ${hotel?.city}")
        Log.d("HOTEL_BOOKING", "========================================")
        
        if (hotel == null) {
            Toast.makeText(this, "Erreur: Hôtel non trouvé", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    private fun setupDatePickers() {
        // Check-in date picker
        binding.etCheckInDate.setOnClickListener {
            showDatePicker(checkInDate) { selectedDate ->
                checkInDate = selectedDate
                
                // S'assurer que check-out est après check-in
                if (checkOutDate.timeInMillis <= checkInDate.timeInMillis) {
                    checkOutDate = Calendar.getInstance().apply {
                        timeInMillis = checkInDate.timeInMillis
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
                
                updateDates()
            }
        }

        // Check-out date picker
        binding.etCheckOutDate.setOnClickListener {
            showDatePicker(checkOutDate, checkInDate.timeInMillis) { selectedDate ->
                checkOutDate = selectedDate
                updateDates()
            }
        }
    }

    private fun showDatePicker(
        currentDate: Calendar,
        minDate: Long = System.currentTimeMillis(),
        onDateSelected: (Calendar) -> Unit
    ) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                onDateSelected(selectedDate)
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.show()
    }

    private fun setupGuestCounters() {
        // Adults counter
        binding.btnDecreaseAdults.setOnClickListener {
            if (adultsCount > 1) {
                adultsCount--
                updateGuestCounts()
            }
        }

        binding.btnIncreaseAdults.setOnClickListener {
            if (adultsCount < 10) {
                adultsCount++
                updateGuestCounts()
            }
        }

        // Children counter
        binding.btnDecreaseChildren.setOnClickListener {
            if (children.isNotEmpty()) {
                children.removeAt(children.size - 1)
                updateGuestCounts()
            }
        }

        binding.btnIncreaseChildren.setOnClickListener {
            if (children.size < 10) {
                showChildAgeDialog()
            }
        }
    }

    private fun showChildAgeDialog() {
        val dialogBinding = DialogChildAgeBinding.inflate(LayoutInflater.from(this))
        
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            val ageText = dialogBinding.etChildAge.text.toString()
            
            if (ageText.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer l'âge de l'enfant", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            if (age == null || age < 0 || age > 17) {
                Toast.makeText(this, "Âge invalide (0-17 ans)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            children.add(Child(age))
            updateGuestCounts()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupViewRoomsButton() {
        binding.btnViewRooms.setOnClickListener {
            val numberOfNights = calculateNumberOfNights()
            
            if (numberOfNights <= 0) {
                Toast.makeText(this, "Veuillez sélectionner des dates valides", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Naviguer vers l'écran de sélection de chambres
            val intent = Intent(this, RoomSelectionActivity::class.java).apply {
                putExtra("hotel", hotel)
                putExtra("checkInDate", dateFormat.format(checkInDate.time))
                putExtra("checkOutDate", dateFormat.format(checkOutDate.time))
                putExtra("numberOfNights", numberOfNights)
                putExtra("adultsCount", adultsCount)
                putParcelableArrayListExtra("children", ArrayList(children))
            }
            startActivity(intent)
        }
    }

    private fun updateUI() {
        hotel?.let { h ->
            binding.tvHotelName.text = h.name
            binding.tvHotelCity.text = "${h.city}, ${h.country ?: ""}"
            binding.tvHotelRating.text = "${h.rating}/5"
            binding.tvHotelDescription.text = h.description ?: "Hôtel confortable avec toutes les commodités nécessaires pour un séjour agréable."

            // Charger l'image
            Glide.with(this)
                .load(h.imageUrl)
                .centerCrop()
                .into(binding.ivHotelImage)
        }

        updateDates()
        updateGuestCounts()
    }

    private fun updateDates() {
        binding.etCheckInDate.setText(dateFormat.format(checkInDate.time))
        binding.etCheckOutDate.setText(dateFormat.format(checkOutDate.time))
        
        val nights = calculateNumberOfNights()
        binding.tvNumberOfNights.text = "$nights nuit${if (nights > 1) "s" else ""}"
    }

    private fun updateGuestCounts() {
        binding.tvAdultsCount.text = adultsCount.toString()
        binding.tvChildrenCount.text = children.size.toString()
    }

    private fun calculateNumberOfNights(): Int {
        val diffInMillis = checkOutDate.timeInMillis - checkInDate.timeInMillis
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }
}
