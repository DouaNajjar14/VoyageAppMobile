package com.example.voyageproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class PaymentReceiptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_receipt)

        // Récupérer les données de l'intent
        val reservationId = intent.getStringExtra("reservationId") ?: ""
        val transactionId = intent.getStringExtra("transactionId") ?: ""
        val offerName = intent.getStringExtra("offerName") ?: ""
        val offerType = intent.getStringExtra("offerType") ?: ""
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        val paymentMethod = intent.getStringExtra("paymentMethod") ?: ""
        val checkInDate = intent.getStringExtra("checkInDate") ?: ""
        val checkOutDate = intent.getStringExtra("checkOutDate") ?: ""
        val numberOfNights = intent.getIntExtra("numberOfNights", 0)
        val numberOfAdults = intent.getIntExtra("numberOfAdults", 0)
        val numberOfChildren = intent.getIntExtra("numberOfChildren", 0)

        // Afficher les informations
        findViewById<TextView>(R.id.tvTransactionId).text = "Transaction: $transactionId"
        findViewById<TextView>(R.id.tvReservationId).text = reservationId.take(8)
        findViewById<TextView>(R.id.tvOfferName).text = offerName
        findViewById<TextView>(R.id.tvOfferType).text = "Type: ${offerType.uppercase()}"
        
        // Prix en TND
        val priceFormatted = String.format("%.0f TND", totalPrice)
        findViewById<TextView>(R.id.tvTotalPrice).text = priceFormatted
        
        // Méthode de paiement
        val paymentMethodText = when (paymentMethod) {
            "CARD" -> "💳 Carte bancaire"
            "PAYPAL" -> "💰 PayPal"
            else -> paymentMethod
        }
        findViewById<TextView>(R.id.tvPaymentMethod).text = paymentMethodText
        
        // Afficher les détails de l'hôtel si disponibles
        val layoutHotelDetails = findViewById<LinearLayout>(R.id.layoutHotelDetails)
        if (offerType.equals("hotel", ignoreCase = true) && checkInDate.isNotEmpty()) {
            layoutHotelDetails.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvDates).text = "Du $checkInDate au $checkOutDate"
            findViewById<TextView>(R.id.tvNights).text = "$numberOfNights nuit(s)"
            findViewById<TextView>(R.id.tvGuests).text = "$numberOfAdults adulte(s), $numberOfChildren enfant(s)"
        } else {
            layoutHotelDetails.visibility = View.GONE
        }
        
        val currentDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        findViewById<TextView>(R.id.tvPaymentDate).text = currentDate

        // Bouton pour voir l'historique - Navigation vers ReservationHistoryActivity
        findViewById<Button>(R.id.btnViewHistory).setOnClickListener {
            val intent = Intent(this, ReservationHistoryActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // Bouton retour à l'accueil
        findViewById<Button>(R.id.btnBackHome).setOnClickListener {
            val intent = Intent(this, com.example.voyageproject.ui.main.MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
