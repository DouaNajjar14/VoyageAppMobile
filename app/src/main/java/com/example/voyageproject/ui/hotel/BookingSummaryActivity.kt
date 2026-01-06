package com.example.voyageproject.ui.hotel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.voyageproject.databinding.ActivityBookingSummaryBinding
import com.example.voyageproject.model.BookingDetails
import com.example.voyageproject.ui.payment.PaymentActivity

class BookingSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingSummaryBinding
    private var bookingDetails: BookingDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        getBookingDetails()
        displaySummary()
        setupPaymentButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Récapitulatif"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun getBookingDetails() {
        bookingDetails = intent.getParcelableExtra("bookingDetails")
    }

    private fun displaySummary() {
        bookingDetails?.let { details ->
            // Hotel info
            binding.tvHotelName.text = details.hotelName
            binding.tvHotelCity.text = details.hotelCity

            // Stay dates
            binding.tvCheckInDate.text = details.checkInDate
            binding.tvCheckOutDate.text = details.checkOutDate
            binding.tvNumberOfNights.text = "${details.numberOfNights} nuit${if (details.numberOfNights > 1) "s" else ""}"

            // Room info
            binding.tvRoomType.text = details.roomType
            binding.tvGuestsInfo.text = "${details.numberOfAdults} adulte${if (details.numberOfAdults > 1) "s" else ""}" +
                    if (details.numberOfChildren > 0) " • ${details.numberOfChildren} enfant${if (details.numberOfChildren > 1) "s" else ""}" else ""
            binding.tvViewType.text = details.viewLabel
            binding.tvMealPlan.text = details.mealLabel

            // Price breakdown
            binding.tvRoomPrice.text = "${details.basePrice.toInt()} TND"
            binding.tvViewPrice.text = "${details.viewPrice.toInt()} TND"
            binding.tvMealPrice.text = "${details.mealPrice.toInt()} TND"
            binding.tvTotalPrice.text = "${details.totalPrice.toInt()} TND"
        }
    }

    private fun setupPaymentButton() {
        binding.btnContinueToPayment.setOnClickListener {
            bookingDetails?.let { details ->
                Log.d("BOOKING_SUMMARY", "========================================")
                Log.d("BOOKING_SUMMARY", "Envoi vers PaymentActivity:")
                Log.d("BOOKING_SUMMARY", "  Hotel ID: ${details.hotelId}")
                Log.d("BOOKING_SUMMARY", "  Hotel Name: ${details.hotelName}")
                Log.d("BOOKING_SUMMARY", "  Room Type: ${details.roomType}")
                Log.d("BOOKING_SUMMARY", "  Total Price: ${details.totalPrice}")
                Log.d("BOOKING_SUMMARY", "========================================")
                
                // Calculer le détail des prix
                val pricePerNight = if (details.numberOfNights > 0) {
                    details.totalPrice / details.numberOfNights
                } else {
                    details.totalPrice
                }
                
                // Calculer le supplément repas correctement
                // Enfants 0-12 ans : gratuits pour les repas
                val mealPricePerAdult = when (details.mealPlan.lowercase()) {
                    "petit_dejeuner" -> 15.0
                    "demi_pension" -> 35.0
                    "pension_complete" -> 55.0
                    "all_inclusive" -> 80.0
                    else -> 0.0
                }
                
                // Seuls les adultes paient les repas (enfants < 12 ans gratuits)
                val mealSupplement = mealPricePerAdult * details.numberOfAdults
                
                // Estimer le supplément vue
                val viewSupplement = when (details.viewType.lowercase()) {
                    "mer" -> 20.0
                    "montagne" -> 15.0
                    else -> 0.0
                }
                
                val basePrice = pricePerNight - viewSupplement - mealSupplement
                
                // Pour les hôtels, nous devons d'abord créer une réservation
                // puis passer au paiement avec l'ID de réservation
                val intent = Intent(this, PaymentActivity::class.java).apply {
                    putExtra("offerType", "hotel")
                    putExtra("offerId", details.hotelId)
                    putExtra("offerName", "${details.hotelName} - ${details.roomType}")
                    putExtra("offerPrice", details.totalPrice)
                    
                    // Ajouter tous les détails de la réservation
                    putExtra("hotelId", details.hotelId)
                    putExtra("hotelName", details.hotelName)
                    putExtra("roomType", details.roomType)
                    putExtra("checkInDate", details.checkInDate)
                    putExtra("checkOutDate", details.checkOutDate)
                    putExtra("numberOfNights", details.numberOfNights)
                    putExtra("numberOfAdults", details.numberOfAdults)
                    putExtra("numberOfChildren", details.numberOfChildren)
                    putExtra("viewType", details.viewType)
                    putExtra("mealPlan", details.mealPlan)
                    
                    // Ajouter le détail des prix
                    putExtra("basePrice", basePrice)
                    putExtra("viewSupplement", viewSupplement)
                    putExtra("mealSupplement", mealSupplement)
                    putExtra("pricePerNight", pricePerNight)
                    
                    putExtra("offerDetails", buildDetailsString(details))
                }
                startActivity(intent)
            }
        }
    }

    private fun buildDetailsString(details: BookingDetails): String {
        return """
            ${details.roomType}
            ${details.checkInDate} - ${details.checkOutDate} (${details.numberOfNights} nuits)
            ${details.numberOfAdults} adulte(s)${if (details.numberOfChildren > 0) ", ${details.numberOfChildren} enfant(s)" else ""}
            ${details.viewLabel}
            ${details.mealLabel}
        """.trimIndent()
    }
}
