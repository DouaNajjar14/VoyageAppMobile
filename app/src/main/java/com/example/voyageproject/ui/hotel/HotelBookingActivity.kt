package com.example.voyageproject.ui.hotel

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.voyageproject.databinding.ActivityHotelBookingBinding
import com.example.voyageproject.ui.payment.PaymentActivity
import com.example.voyageproject.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class HotelBookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHotelBookingBinding
    private lateinit var session: SessionManager
    
    private var hotelId: String = ""
    private var hotelName: String = ""
    private var roomId: String = ""
    private var roomName: String = ""
    private var pricePerNight: Double = 0.0
    private var maxGuests: Int = 2
    
    private var checkInDate: Calendar? = null
    private var checkOutDate: Calendar? = null
    private var adultsCount = 1
    private var childrenCount = 0
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityHotelBookingBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            session = SessionManager(this)
            
            // Récupérer données
            hotelId = intent.getStringExtra("hotelId") ?: ""
            hotelName = intent.getStringExtra("hotelName") ?: ""
            roomId = intent.getStringExtra("roomId") ?: ""
            roomName = intent.getStringExtra("roomName") ?: ""
            pricePerNight = intent.getDoubleExtra("pricePerNight", 0.0)
            maxGuests = intent.getIntExtra("maxGuests", 2)
            
            Log.d("BOOKING", "Hotel: $hotelName, Room: $roomName, Price: $pricePerNight")
            
            setupToolbar()
            displayBookingSummary()
            setupDatePickers()
            setupGuestCounters()
            prefillGuestInfo()
            setupContinueButton()
            
        } catch (e: Exception) {
            Log.e("BOOKING", "Erreur: ${e.message}", e)
            Toast.makeText(this, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
    
    private fun displayBookingSummary() {
        binding.tvHotelName.text = hotelName
        binding.tvRoomName.text = roomName
        binding.tvPricePerNight.text = "${pricePerNight.toInt()} €"
        updatePriceCalculation()
    }
    
    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        
        binding.etCheckIn.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    checkInDate = Calendar.getInstance().apply {
                        set(year, month, day)
                    }
                    binding.etCheckIn.setText(dateFormat.format(checkInDate!!.time))
                    
                    if (checkOutDate != null && checkOutDate!!.before(checkInDate)) {
                        checkOutDate = null
                        binding.etCheckOut.setText("")
                    }
                    
                    updatePriceCalculation()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = Calendar.getInstance().timeInMillis
                show()
            }
        }
        
        binding.etCheckOut.setOnClickListener {
            if (checkInDate == null) {
                Toast.makeText(this, "Sélectionnez d'abord la date d'arrivée", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val minDate = (checkInDate!!.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, 1)
            }
            
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    checkOutDate = Calendar.getInstance().apply {
                        set(year, month, day)
                    }
                    binding.etCheckOut.setText(dateFormat.format(checkOutDate!!.time))
                    updatePriceCalculation()
                },
                minDate.get(Calendar.YEAR),
                minDate.get(Calendar.MONTH),
                minDate.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = minDate.timeInMillis
                show()
            }
        }
    }
    
    private fun setupGuestCounters() {
        binding.btnDecreaseAdults.setOnClickListener {
            if (adultsCount > 1) {
                adultsCount--
                updateGuestCounts()
            }
        }
        
        binding.btnIncreaseAdults.setOnClickListener {
            if (adultsCount + childrenCount < maxGuests) {
                adultsCount++
                updateGuestCounts()
            } else {
                Toast.makeText(this, "Capacité max: $maxGuests", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnDecreaseChildren.setOnClickListener {
            if (childrenCount > 0) {
                childrenCount--
                updateGuestCounts()
            }
        }
        
        binding.btnIncreaseChildren.setOnClickListener {
            if (adultsCount + childrenCount < maxGuests) {
                childrenCount++
                updateGuestCounts()
            } else {
                Toast.makeText(this, "Capacité max: $maxGuests", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateGuestCounts() {
        binding.tvAdultsCount.text = adultsCount.toString()
        binding.tvChildrenCount.text = childrenCount.toString()
    }
    
    private fun prefillGuestInfo() {
        session.getEmail()?.let {
            binding.etEmail.setText(it)
        }
    }
    
    private fun updatePriceCalculation() {
        val nights = calculateNumberOfNights()
        
        if (nights > 0) {
            binding.tvNumberOfNights.text = "$nights ${if (nights > 1) "nuits" else "nuit"}"
            val totalPrice = pricePerNight * nights
            binding.tvTotalPrice.text = "${totalPrice.toInt()} €"
        } else {
            binding.tvNumberOfNights.text = "0 nuits"
            binding.tvTotalPrice.text = "0 €"
        }
    }
    
    private fun calculateNumberOfNights(): Int {
        if (checkInDate == null || checkOutDate == null) return 0
        
        val diffInMillis = checkOutDate!!.timeInMillis - checkInDate!!.timeInMillis
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }
    
    private fun setupContinueButton() {
        binding.btnContinueToPayment.setOnClickListener {
            if (validateForm()) {
                proceedToPayment()
            }
        }
    }
    
    private fun validateForm(): Boolean {
        if (checkInDate == null) {
            Toast.makeText(this, "Sélectionnez la date d'arrivée", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (checkOutDate == null) {
            Toast.makeText(this, "Sélectionnez la date de départ", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (calculateNumberOfNights() <= 0) {
            Toast.makeText(this, "Date de départ invalide", Toast.LENGTH_SHORT).show()
            return false
        }
        
        val firstName = binding.etFirstName.text.toString().trim()
        if (firstName.isEmpty()) {
            Toast.makeText(this, "Entrez votre prénom", Toast.LENGTH_SHORT).show()
            binding.etFirstName.requestFocus()
            return false
        }
        
        val lastName = binding.etLastName.text.toString().trim()
        if (lastName.isEmpty()) {
            Toast.makeText(this, "Entrez votre nom", Toast.LENGTH_SHORT).show()
            binding.etLastName.requestFocus()
            return false
        }
        
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
            return false
        }
        
        val phone = binding.etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            Toast.makeText(this, "Entrez votre téléphone", Toast.LENGTH_SHORT).show()
            binding.etPhone.requestFocus()
            return false
        }
        
        return true
    }
    
    private fun proceedToPayment() {
        val nights = calculateNumberOfNights()
        val totalPrice = pricePerNight * nights
        val details = "${dateFormat.format(checkInDate!!.time)} - ${dateFormat.format(checkOutDate!!.time)}\n" +
                     "$nights ${if (nights > 1) "nuits" else "nuit"} • " +
                     "${adultsCount + childrenCount} ${if (adultsCount + childrenCount > 1) "personnes" else "personne"}"
        
        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("offerType", "hotel")
            putExtra("offerId", hotelId)
            putExtra("offerName", "$hotelName - $roomName")
            putExtra("offerPrice", totalPrice)
            putExtra("offerDetails", details)
        }
        startActivity(intent)
    }
}
