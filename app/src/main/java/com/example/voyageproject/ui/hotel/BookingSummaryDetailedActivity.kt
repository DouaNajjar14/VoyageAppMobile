package com.example.voyageproject.ui.hotel

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ActivityBookingSummaryDetailedBinding
import com.example.voyageproject.model.BookingDetails
import com.example.voyageproject.model.Child
import com.example.voyageproject.ui.payment.PaymentActivity

class BookingSummaryDetailedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingSummaryDetailedBinding
    private var bookingDetails: BookingDetails? = null
    private var children: ArrayList<Child> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingSummaryDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        displayBookingDetails()
        setupButtons()
    }

    private fun getIntentData() {
        bookingDetails = intent.getParcelableExtra("bookingDetails")
        children = intent.getParcelableArrayListExtra("children") ?: arrayListOf()
    }

    private fun displayBookingDetails() {
        val details = bookingDetails ?: return

        // Informations de l'hôtel
        binding.tvHotelName.text = details.hotelName
        binding.tvHotelCity.text = details.hotelCity

        // Dates
        binding.tvCheckInDate.text = details.checkInDate
        binding.tvCheckOutDate.text = details.checkOutDate
        binding.tvNumberOfNights.text = "${details.numberOfNights} nuit${if (details.numberOfNights > 1) "s" else ""}"

        // Chambre et options
        binding.tvRoomType.text = details.roomType
        binding.tvViewType.text = details.viewLabel
        binding.tvMealPlan.text = details.mealLabel

        // Voyageurs
        binding.tvAdultsCount.text = "${details.numberOfAdults} adulte${if (details.numberOfAdults > 1) "s" else ""}"
        
        // Afficher les détails des enfants
        displayChildrenDetails()

        // Prix
        val basePrice = details.basePrice / details.numberOfNights
        val viewPrice = details.viewPrice / details.numberOfNights
        val mealPrice = details.mealPrice / details.numberOfNights
        val pricePerNight = basePrice + viewPrice + mealPrice

        binding.tvBasePrice.text = "${basePrice.toInt()} TND/nuit"
        binding.tvViewPrice.text = "+${viewPrice.toInt()} TND/nuit"
        binding.tvMealPrice.text = "+${mealPrice.toInt()} TND/nuit"
        binding.tvPricePerNight.text = "${pricePerNight.toInt()} TND/nuit"

        // Calcul détaillé du prix
        displayPricingBreakdown(pricePerNight, details.numberOfNights, details.numberOfAdults)

        // Prix total
        binding.tvTotalPrice.text = "${details.totalPrice.toInt()} TND"
    }

    private fun displayChildrenDetails() {
        binding.layoutChildrenDetails.removeAllViews()

        if (children.isEmpty()) {
            return
        }

        children.forEachIndexed { index, child ->
            val childView = LayoutInflater.from(this).inflate(
                R.layout.item_child_detail,
                binding.layoutChildrenDetails,
                false
            ) as LinearLayout

            val tvLabel = childView.findViewById<TextView>(R.id.tvChildLabel)
            val tvValue = childView.findViewById<TextView>(R.id.tvChildValue)

            tvLabel.text = "Enfant ${index + 1} (${child.age} ans)"
            tvValue.text = if (child.isFree()) "Gratuit" else "50% du tarif"

            binding.layoutChildrenDetails.addView(childView)
        }
    }

    private fun displayPricingBreakdown(pricePerNight: Double, nights: Int, adults: Int) {
        // Prix pour les adultes
        binding.layoutAdultsPricing.removeAllViews()
        
        val adultsView = createPricingRow(
            "${pricePerNight.toInt()} TND × $nights nuit${if (nights > 1) "s" else ""} × $adults adulte${if (adults > 1) "s" else ""}",
            "${(pricePerNight * nights * adults).toInt()} TND"
        )
        binding.layoutAdultsPricing.addView(adultsView)

        // Prix pour les enfants - regroupés par catégorie
        binding.layoutChildrenPricing.removeAllViews()
        
        // Compter les enfants gratuits et payants
        val freeChildren = children.filter { it.isFree() }
        val payingChildren = children.filter { !it.isFree() }
        
        // Afficher les enfants gratuits (<12 ans)
        if (freeChildren.isNotEmpty()) {
            val freeChildrenView = createPricingRow(
                "${freeChildren.size} enfant${if (freeChildren.size > 1) "s" else ""} (<12 ans) - Gratuit",
                "0 TND"
            )
            binding.layoutChildrenPricing.addView(freeChildrenView)
        }
        
        // Afficher les enfants payants (≥12 ans)
        if (payingChildren.isNotEmpty()) {
            val halfPrice = pricePerNight / 2
            val childrenPrice = halfPrice * nights * payingChildren.size
            
            val label = "${halfPrice.toInt()} TND × $nights nuit${if (nights > 1) "s" else ""} × ${payingChildren.size} enfant${if (payingChildren.size > 1) "s" else ""}"
            
            val payingChildrenView = createPricingRow(label, "${childrenPrice.toInt()} TND")
            binding.layoutChildrenPricing.addView(payingChildrenView)
        }
    }

    private fun createPricingRow(label: String, value: String): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = resources.getDimensionPixelSize(R.dimen.spacing_small)
            }
        }

        val tvLabel = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            text = label
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
            textSize = 14f
        }

        val tvValue = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = value
            setTextColor(resources.getColor(android.R.color.black, null))
            textSize = 14f
        }

        layout.addView(tvLabel)
        layout.addView(tvValue)

        return layout
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnProceedToPayment.setOnClickListener {
            val details = bookingDetails ?: return@setOnClickListener

            val intent = Intent(this, PaymentActivity::class.java).apply {
                putExtra("offerType", "hotel")
                putExtra("offerId", details.hotelId)
                putExtra("offerName", details.hotelName)
                putExtra("offerPrice", details.totalPrice)
                putExtra("hotelId", details.hotelId)
                putExtra("roomType", details.roomType)
                putExtra("checkInDate", details.checkInDate)
                putExtra("checkOutDate", details.checkOutDate)
                putExtra("numberOfNights", details.numberOfNights)
                putExtra("numberOfAdults", details.numberOfAdults)
                putExtra("numberOfChildren", children.size)
                putExtra("viewType", details.viewType)
                putExtra("mealPlan", details.mealPlan)
                putExtra("formula", details.mealPlan)
            }
            startActivity(intent)
        }
    }
}
