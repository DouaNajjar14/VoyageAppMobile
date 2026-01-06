package com.example.voyageproject.ui.payment

import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.databinding.ActivityPaymentBookingStyleBinding
import com.example.voyageproject.repository.ReservationRepository
import com.example.voyageproject.ui.main.MainActivity
import com.example.voyageproject.utils.SessionManager
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBookingStyleBinding
    private val reservationRepo = ReservationRepository()
    private lateinit var session: SessionManager

    private var offerType: String = ""
    private var offerId: String = ""
    private var offerName: String = ""
    private var offerPrice: Double = 0.0
    private var offerDetails: String = ""
    
    // Détails spécifiques pour les réservations d'hôtel
    private var hotelId: String = ""
    private var roomType: String = ""
    private var checkInDate: String = ""
    private var checkOutDate: String = ""
    private var numberOfNights: Int = 0
    private var numberOfAdults: Int = 0
    private var numberOfChildren: Int = 0
    private var viewType: String = ""
    private var mealPlan: String = ""
    
    // Détails spécifiques pour les réservations de vol
    private var departureDate: String = ""
    private var returnDate: String = ""
    private var adultsCount: Int = 0
    private var childrenCount: Int = 0
    private var flightClass: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBookingStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        setupToolbar()
        getIntentData()
        displayOfferSummary()
        setupPaymentMethodToggle()
        setupPayButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun getIntentData() {
        offerType = intent.getStringExtra("offerType") ?: ""
        offerId = intent.getStringExtra("offerId") ?: ""
        offerName = intent.getStringExtra("offerName") ?: ""
        offerPrice = intent.getDoubleExtra("offerPrice", 0.0)
        offerDetails = intent.getStringExtra("offerDetails") ?: ""
        
        // Récupérer les détails spécifiques pour les hôtels
        if (offerType == "hotel") {
            hotelId = intent.getStringExtra("hotelId") ?: offerId
            roomType = intent.getStringExtra("roomType") ?: ""
            checkInDate = intent.getStringExtra("checkInDate") ?: ""
            checkOutDate = intent.getStringExtra("checkOutDate") ?: ""
            numberOfNights = intent.getIntExtra("numberOfNights", 0)
            numberOfAdults = intent.getIntExtra("numberOfAdults", 0)
            numberOfChildren = intent.getIntExtra("numberOfChildren", 0)
            viewType = intent.getStringExtra("viewType") ?: ""
            mealPlan = intent.getStringExtra("mealPlan") ?: ""
            
            Log.d("PAYMENT", "=== DONNÉES HÔTEL REÇUES ===")
            Log.d("PAYMENT", "hotelId: $hotelId")
            Log.d("PAYMENT", "roomType: $roomType")
            Log.d("PAYMENT", "checkInDate: $checkInDate")
            Log.d("PAYMENT", "checkOutDate: $checkOutDate")
            Log.d("PAYMENT", "numberOfNights: $numberOfNights")
            Log.d("PAYMENT", "numberOfAdults: $numberOfAdults")
            Log.d("PAYMENT", "numberOfChildren: $numberOfChildren")
            Log.d("PAYMENT", "viewType: $viewType")
            Log.d("PAYMENT", "mealPlan: $mealPlan")
            Log.d("PAYMENT", "basePrice: ${intent.getDoubleExtra("basePrice", 0.0)}")
            Log.d("PAYMENT", "viewSupplement: ${intent.getDoubleExtra("viewSupplement", 0.0)}")
            Log.d("PAYMENT", "mealSupplement: ${intent.getDoubleExtra("mealSupplement", 0.0)}")
            Log.d("PAYMENT", "pricePerNight: ${intent.getDoubleExtra("pricePerNight", 0.0)}")
            Log.d("PAYMENT", "============================")
        }
        
        // Récupérer les détails spécifiques pour les vols
        if (offerType == "flight") {
            departureDate = intent.getStringExtra("departure_date") ?: ""
            returnDate = intent.getStringExtra("return_date") ?: ""
            adultsCount = intent.getIntExtra("adults_count", 0)
            childrenCount = intent.getIntExtra("children_count", 0)
            flightClass = intent.getStringExtra("flight_class") ?: ""
        }
        
        Log.d("PAYMENT", "Type: $offerType, ID: $offerId, Prix: $offerPrice")
        if (offerType == "hotel") {
            Log.d("PAYMENT", "Hôtel: $hotelId, Chambre: $roomType, Dates: $checkInDate - $checkOutDate")
        }
        if (offerType == "flight") {
            Log.d("PAYMENT", "Vol: $offerId, Classe: $flightClass, Date: $departureDate, Passagers: $adultsCount adultes, $childrenCount enfants")
        }
    }

    private fun displayOfferSummary() {
        binding.tvOfferName.text = offerName
        binding.tvOfferPrice.text = "${offerPrice.toInt()} TND"
        
        // Construire les détails selon le type d'offre
        val details = when (offerType) {
            "circuit" -> buildCircuitDetails()
            "hotel" -> buildHotelDetails()
            "flight" -> buildFlightDetails()
            else -> offerDetails
        }
        
        if (details.isNotEmpty()) {
            binding.tvOfferDetails.text = details
            binding.tvOfferDetails.visibility = View.VISIBLE
        } else {
            binding.tvOfferDetails.visibility = View.GONE
        }
    }
    
    private fun buildCircuitDetails(): String {
        val adults = intent.getIntExtra("adults", 0)
        val childrenAgesList = intent.getIntegerArrayListExtra("childrenAges") ?: arrayListOf()
        val duration = intent.getIntExtra("circuitDuration", 0)
        val destination = intent.getStringExtra("circuitDestination") ?: ""
        val dateMillis = intent.getLongExtra("departureDate", 0L)
        
        return buildString {
            if (destination.isNotEmpty()) {
                append("📍 $destination\n")
            }
            if (duration > 0) {
                append("📅 $duration jours / ${duration - 1} nuits\n")
            }
            if (dateMillis > 0) {
                val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                val startDate = java.util.Date(dateMillis)
                val calendar = java.util.Calendar.getInstance()
                calendar.time = startDate
                calendar.add(java.util.Calendar.DAY_OF_YEAR, duration - 1)
                val endDate = calendar.time
                append("Du ${sdf.format(startDate)} au ${sdf.format(endDate)}\n")
            }
            append("👥 $adults adulte${if (adults > 1) "s" else ""}")
            if (childrenAgesList.isNotEmpty()) {
                append(", ${childrenAgesList.size} enfant${if (childrenAgesList.size > 1) "s" else ""}")
            }
        }
    }
    
    private fun buildHotelDetails(): String {
        return buildString {
            if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty()) {
                append("📅 $checkInDate - $checkOutDate\n")
            }
            if (numberOfNights > 0) {
                append("🌙 $numberOfNights nuit${if (numberOfNights > 1) "s" else ""}\n")
            }
            append("👥 $numberOfAdults adulte${if (numberOfAdults > 1) "s" else ""}")
            if (numberOfChildren > 0) {
                append(", $numberOfChildren enfant${if (numberOfChildren > 1) "s" else ""}")
            }
            if (roomType.isNotEmpty()) {
                append("\n🛏️ $roomType")
            }
        }
    }
    
    private fun buildFlightDetails(): String {
        return buildString {
            if (departureDate.isNotEmpty()) {
                append("📅 Départ: $departureDate\n")
            }
            if (returnDate.isNotEmpty()) {
                append("📅 Retour: $returnDate\n")
            }
            append("👥 $adultsCount adulte${if (adultsCount > 1) "s" else ""}")
            if (childrenCount > 0) {
                append(", $childrenCount enfant${if (childrenCount > 1) "s" else ""}")
            }
            if (flightClass.isNotEmpty()) {
                append("\n✈️ Classe: $flightClass")
            }
        }
    }

    private fun setupPaymentMethodToggle() {
        binding.radioGroupPayment.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbCard.id -> {
                    binding.layoutCardDetails.visibility = View.VISIBLE
                }
                else -> {
                    binding.layoutCardDetails.visibility = View.GONE
                }
            }
        }
    }

    private fun setupPayButton() {
        binding.btnPay.setOnClickListener {
            processPayment()
        }
        
        // Contrôle de saisie en temps réel pour la date d'expiration
        binding.etExpiry.addTextChangedListener(object : android.text.TextWatcher {
            private var isFormatting = false
            private var deletingSlash = false
            
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Détecter si on supprime le slash
                if (count == 1 && after == 0 && s?.getOrNull(start) == '/') {
                    deletingSlash = true
                }
            }
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: android.text.Editable?) {
                if (isFormatting) return
                
                isFormatting = true
                
                val input = s.toString().replace("/", "").filter { it.isDigit() }
                
                // Si on supprime le slash, supprimer aussi le chiffre avant
                if (deletingSlash && input.length >= 2) {
                    s?.clear()
                    s?.append(input.substring(0, input.length - 1))
                    deletingSlash = false
                    isFormatting = false
                    return
                }
                
                val formatted = when {
                    input.isEmpty() -> ""
                    input.length <= 2 -> input
                    else -> "${input.substring(0, 2)}/${input.substring(2, minOf(4, input.length))}"
                }
                
                s?.clear()
                s?.append(formatted)
                
                // Validation en temps réel
                if (formatted.length == 5) {
                    validateExpiryDate(formatted)
                } else if (formatted.isNotEmpty()) {
                    binding.etExpiry.error = null
                }
                
                isFormatting = false
            }
        })
        
        // Contrôle de saisie pour le nom (lettres et espaces uniquement)
        binding.etCardName.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: android.text.Editable?) {
                val input = s.toString()
                
                // Supprimer les caractères non autorisés
                val filtered = input.filter { it.isLetter() || it.isWhitespace() }
                
                if (input != filtered) {
                    s?.clear()
                    s?.append(filtered)
                }
                
                // Validation en temps réel
                if (filtered.isNotEmpty()) {
                    val nameParts = filtered.trim().split(" ").filter { it.isNotEmpty() }
                    if (nameParts.size >= 2) {
                        binding.etCardName.error = null
                    } else if (filtered.trim().contains(" ")) {
                        binding.etCardName.error = "Prénom et nom requis"
                    }
                }
            }
        })
        
        // Contrôle pour le numéro de carte (chiffres uniquement)
        binding.etCardNumber.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: android.text.Editable?) {
                val input = s.toString()
                val filtered = input.filter { it.isDigit() }
                
                if (input != filtered) {
                    s?.clear()
                    s?.append(filtered)
                }
                
                // Validation en temps réel
                if (filtered.length == 16) {
                    binding.etCardNumber.error = null
                } else if (filtered.length > 16) {
                    s?.delete(16, s.length)
                }
            }
        })
        
        // Contrôle pour le CVV (chiffres uniquement)
        binding.etCvv.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: android.text.Editable?) {
                val input = s.toString()
                val filtered = input.filter { it.isDigit() }
                
                if (input != filtered) {
                    s?.clear()
                    s?.append(filtered)
                }
                
                // Validation en temps réel
                if (filtered.length == 3) {
                    binding.etCvv.error = null
                } else if (filtered.length > 3) {
                    s?.delete(3, s.length)
                }
            }
        })
    }
    
    private fun validateExpiryDate(expiry: String): Boolean {
        try {
            val parts = expiry.split("/")
            if (parts.size != 2) {
                binding.etExpiry.error = "Format: MM/AA"
                return false
            }
            
            val month = parts[0].toIntOrNull()
            val year = parts[1].toIntOrNull()
            
            if (month == null || year == null) {
                binding.etExpiry.error = "Format invalide"
                return false
            }
            
            if (month < 1 || month > 12) {
                binding.etExpiry.error = "Mois invalide (01-12)"
                return false
            }
            
            // Obtenir l'année et le mois actuels
            val calendar = java.util.Calendar.getInstance()
            val currentYear = calendar.get(java.util.Calendar.YEAR) % 100
            val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
            
            // Vérifier si la carte est expirée
            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                binding.etExpiry.error = "Carte expirée"
                return false
            }
            
            binding.etExpiry.error = null
            return true
        } catch (e: Exception) {
            binding.etExpiry.error = "Format invalide"
            return false
        }
    }

    private fun processPayment() {
        Log.d("PAYMENT", "=== Début paiement ===")
        Log.d("PAYMENT", "Type offre: $offerType")
        Log.d("PAYMENT", "ID offre: $offerId")
        Log.d("PAYMENT", "Nom offre: $offerName")
        Log.d("PAYMENT", "Prix: $offerPrice")
        
        // Vérifier session
        val email = session.getEmail()
        Log.d("PAYMENT", "Email session: $email")
        
        if (email.isNullOrEmpty()) {
            Log.e("PAYMENT", "Session vide!")
            Toast.makeText(this, "Veuillez vous reconnecter", Toast.LENGTH_LONG).show()
            
            // Rediriger vers login
            val intent = Intent(this, com.example.voyageproject.ui.login.LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Valider carte bancaire
        if (!validateCardDetails()) {
            return
        }
        
        val paymentMethod = "CARD"  // Toujours carte bancaire
        
        Log.d("PAYMENT", "Méthode de paiement: $paymentMethod")

        binding.btnPay.isEnabled = false
        binding.btnPay.text = "Vérification du compte..."

        lifecycleScope.launch {
            try {
                // Étape 1: Vérifier que le profil existe et est actif (OPTIONNEL)
                Log.d("PAYMENT", "=== Vérification du profil (optionnelle) ===")
                try {
                    val profileResponse = com.example.voyageproject.network.RetrofitClient.api.getProfile(email)
                    Log.d("PAYMENT", "Profil - Code: ${profileResponse.code()}")
                    
                    if (profileResponse.isSuccessful) {
                        val profile = profileResponse.body()
                        Log.d("PAYMENT", "✅ Profil vérifié: ${profile?.email}")
                    } else {
                        Log.w("PAYMENT", "⚠️ Vérification profil échouée (${profileResponse.code()}), mais on continue...")
                    }
                } catch (e: Exception) {
                    Log.w("PAYMENT", "⚠️ Erreur vérification profil: ${e.message}, mais on continue...")
                }
                
                // Étape 2: Procéder au paiement
                runOnUiThread {
                    binding.btnPay.text = "Traitement du paiement..."
                }
                
                val body = mutableMapOf(
                    "email" to email,
                    "paymentMethod" to paymentMethod
                )

                // Ajouter l'ID et les détails selon le type d'offre
                when (offerType.lowercase()) {
                    "hotel" -> {
                        body["hotelId"] = hotelId
                        body["roomType"] = roomType
                        body["checkInDate"] = checkInDate
                        body["checkOutDate"] = checkOutDate
                        body["adultsCount"] = numberOfAdults.toString()
                        body["childrenCount"] = numberOfChildren.toString()
                        body["formula"] = mealPlan
                        body["totalPrice"] = offerPrice.toString()
                        
                        // Créer le détail des prix en JSON pour l'hôtel
                        val priceBreakdown = mutableMapOf<String, Double>()
                        priceBreakdown["basePrice"] = intent.getDoubleExtra("basePrice", 0.0)
                        priceBreakdown["viewSupplement"] = intent.getDoubleExtra("viewSupplement", 0.0)
                        priceBreakdown["mealSupplement"] = intent.getDoubleExtra("mealSupplement", 0.0)
                        priceBreakdown["pricePerNight"] = intent.getDoubleExtra("pricePerNight", 0.0)
                        priceBreakdown["totalPrice"] = offerPrice
                        
                        val priceBreakdownJson = com.google.gson.Gson().toJson(priceBreakdown)
                        body["priceBreakdown"] = priceBreakdownJson
                        
                        Log.d("PAYMENT", "=== RÉSERVATION HÔTEL COMPLÈTE ===")
                        Log.d("PAYMENT", "  - hotelId: $hotelId")
                        Log.d("PAYMENT", "  - roomType: $roomType")
                        Log.d("PAYMENT", "  - dates: $checkInDate - $checkOutDate")
                        Log.d("PAYMENT", "  - adultsCount: $numberOfAdults")
                        Log.d("PAYMENT", "  - childrenCount: $numberOfChildren")
                        Log.d("PAYMENT", "  - formula: $mealPlan")
                        Log.d("PAYMENT", "  - basePrice: ${priceBreakdown["basePrice"]}")
                        Log.d("PAYMENT", "  - viewSupplement: ${priceBreakdown["viewSupplement"]}")
                        Log.d("PAYMENT", "  - mealSupplement: ${priceBreakdown["mealSupplement"]}")
                        Log.d("PAYMENT", "  - pricePerNight: ${priceBreakdown["pricePerNight"]}")
                        Log.d("PAYMENT", "  - détail prix JSON: $priceBreakdownJson")
                        Log.d("PAYMENT", "  - prix total: $offerPrice TND")
                        Log.d("PAYMENT", "====================================")
                    }
                    "flight" -> {
                        body["flightId"] = offerId
                        // Le backend attend checkInDate et checkOutDate pour toutes les réservations
                        body["checkInDate"] = departureDate
                        body["checkOutDate"] = if (returnDate.isNotEmpty()) returnDate else departureDate
                        body["adultsCount"] = adultsCount.toString()
                        body["childrenCount"] = childrenCount.toString()
                        body["flightClass"] = flightClass
                        body["totalPrice"] = offerPrice.toString()
                        
                        Log.d("PAYMENT", "Réservation vol complète:")
                        Log.d("PAYMENT", "  - flightId: $offerId")
                        Log.d("PAYMENT", "  - classe: $flightClass")
                        Log.d("PAYMENT", "  - dates: $departureDate${if (returnDate.isNotEmpty()) " - $returnDate" else ""}")
                        Log.d("PAYMENT", "  - passagers: $adultsCount adultes, $childrenCount enfants")
                        Log.d("PAYMENT", "  - prix total: $offerPrice TND")
                    }
                    "circuit" -> {
                        body["circuitId"] = offerId
                        body["totalPrice"] = offerPrice.toString()
                        
                        // Ajouter tous les détails du circuit
                        val adults = intent.getIntExtra("adults", 0)
                        val childrenAgesList = intent.getIntegerArrayListExtra("childrenAges") ?: arrayListOf()
                        val hotelLevel = intent.getStringExtra("hotelLevel") ?: "STANDARD"
                        val flightClass = intent.getStringExtra("flightClass") ?: "ECONOMY"
                        val departureDate = intent.getLongExtra("departureDate", 0L)
                        val duration = intent.getIntExtra("circuitDuration", 0)
                        
                        body["adultsCount"] = adults.toString()
                        body["childrenCount"] = childrenAgesList.size.toString()
                        
                        // Convertir les âges des enfants en JSON
                        val childrenAgesJson = com.google.gson.Gson().toJson(childrenAgesList)
                        body["childrenAges"] = childrenAgesJson
                        
                        body["hotelLevel"] = hotelLevel
                        body["flightClass"] = flightClass
                        
                        // Calculer et ajouter les dates
                        if (departureDate > 0 && duration > 0) {
                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            val startDate = java.util.Date(departureDate)
                            val calendar = java.util.Calendar.getInstance()
                            calendar.time = startDate
                            calendar.add(java.util.Calendar.DAY_OF_YEAR, duration - 1)
                            val endDate = calendar.time
                            
                            body["checkInDate"] = sdf.format(startDate)
                            body["checkOutDate"] = sdf.format(endDate)
                        }
                        
                        // Ajouter les activités sélectionnées
                        val selectedActivities = intent.getStringExtra("selectedActivities")
                        if (!selectedActivities.isNullOrEmpty()) {
                            body["selectedActivities"] = selectedActivities
                        }
                        
                        // Créer le détail des prix en JSON
                        val priceBreakdown = mutableMapOf<String, Double>()
                        priceBreakdown["basePrice"] = intent.getDoubleExtra("basePrice", 0.0)
                        priceBreakdown["hotelSupplement"] = intent.getDoubleExtra("hotelSupplement", 0.0)
                        priceBreakdown["flightSupplement"] = intent.getDoubleExtra("flightSupplement", 0.0)
                        priceBreakdown["activitiesTotal"] = intent.getDoubleExtra("activitiesTotal", 0.0)
                        priceBreakdown["totalPrice"] = offerPrice
                        
                        val priceBreakdownJson = com.google.gson.Gson().toJson(priceBreakdown)
                        body["priceBreakdown"] = priceBreakdownJson
                        
                        Log.d("PAYMENT", "Réservation circuit complète:")
                        Log.d("PAYMENT", "  - circuitId: $offerId")
                        Log.d("PAYMENT", "  - adultes: $adults, enfants: ${childrenAgesList.size}")
                        Log.d("PAYMENT", "  - âges enfants: $childrenAgesJson")
                        Log.d("PAYMENT", "  - hôtel: $hotelLevel, vol: $flightClass")
                        Log.d("PAYMENT", "  - activités: $selectedActivities")
                        Log.d("PAYMENT", "  - détail prix: $priceBreakdownJson")
                        Log.d("PAYMENT", "  - prix total: $offerPrice TND")
                    }
                    else -> {
                        Log.e("PAYMENT", "Type d'offre inconnu: $offerType")
                        runOnUiThread {
                            Toast.makeText(this@PaymentActivity, "Type d'offre invalide", Toast.LENGTH_LONG).show()
                            binding.btnPay.isEnabled = true
                            binding.btnPay.text = "Payer maintenant"
                        }
                        return@launch
                    }
                }

                Log.d("PAYMENT", "=== Requête de réservation ===")
                Log.d("PAYMENT", "Body: $body")
                
                val response = reservationRepo.bookOffer(body)

                Log.d("PAYMENT", "=== Réponse ===")
                Log.d("PAYMENT", "Code HTTP: ${response.code()}")
                Log.d("PAYMENT", "Message: ${response.message()}")
                
                runOnUiThread {
                    if (response.isSuccessful) {
                        Log.d("PAYMENT", "✅ Paiement réussi!")
                        
                        // Extraire les données de la réponse si disponibles
                        val responseBody = response.body()
                        Log.d("PAYMENT", "Response body: $responseBody")
                        
                        showPaymentReceipt()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("PAYMENT", "❌ Erreur HTTP ${response.code()}")
                        Log.e("PAYMENT", "Message: ${response.message()}")
                        Log.e("PAYMENT", "Body: $errorBody")
                        
                        val errorMessage = when (response.code()) {
                            400 -> "Données invalides - Vérifiez l'ID de l'offre"
                            401 -> "Non autorisé - Votre compte n'a pas les permissions nécessaires"
                            403 -> "Accès refusé - Compte non activé ou suspendu"
                            404 -> "Offre non trouvée - ID: $offerId"
                            500 -> "Erreur serveur - Réessayez plus tard"
                            else -> "Erreur ${response.code()}: ${response.message()}"
                        }
                        
                        Toast.makeText(this@PaymentActivity, errorMessage, Toast.LENGTH_LONG).show()
                        binding.btnPay.isEnabled = true
                        binding.btnPay.text = "Payer maintenant"
                    }
                }
            } catch (e: Exception) {
                Log.e("PAYMENT", "❌ Exception: ${e.javaClass.simpleName}")
                Log.e("PAYMENT", "Message: ${e.message}", e)
                e.printStackTrace()
                
                runOnUiThread {
                    Toast.makeText(this@PaymentActivity, "Erreur de connexion: ${e.message}", Toast.LENGTH_LONG).show()
                    binding.btnPay.isEnabled = true
                    binding.btnPay.text = "Payer maintenant"
                }
            }
        }
    }

    private fun validateCardDetails(): Boolean {
        // Validation numéro de carte
        val cardNumber = binding.etCardNumber.text.toString().trim()
        if (cardNumber.length != 16 || !cardNumber.all { it.isDigit() }) {
            binding.etCardNumber.error = "16 chiffres requis"
            binding.etCardNumber.requestFocus()
            Toast.makeText(this, "Le numéro de carte doit contenir 16 chiffres", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validation nom sur la carte
        val cardName = binding.etCardName.text.toString().trim()
        if (cardName.isEmpty()) {
            binding.etCardName.error = "Nom requis"
            binding.etCardName.requestFocus()
            Toast.makeText(this, "Veuillez entrer le nom sur la carte", Toast.LENGTH_SHORT).show()
            return false
        }
        
        // Vérifier que le nom contient au moins 2 mots (prénom et nom)
        val nameParts = cardName.split(" ").filter { it.isNotEmpty() }
        if (nameParts.size < 2) {
            binding.etCardName.error = "Prénom et nom requis"
            binding.etCardName.requestFocus()
            Toast.makeText(this, "Entrez votre prénom et nom (ex: Jean Dupont)", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validation date d'expiration
        val expiry = binding.etExpiry.text.toString().trim()
        if (!validateExpiryDate(expiry)) {
            binding.etExpiry.requestFocus()
            Toast.makeText(this, "Date d'expiration invalide", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validation CVV
        val cvv = binding.etCvv.text.toString().trim()
        if (cvv.length != 3 || !cvv.all { it.isDigit() }) {
            binding.etCvv.error = "3 chiffres"
            binding.etCvv.requestFocus()
            Toast.makeText(this, "Le CVV doit contenir 3 chiffres", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun showPaymentReceipt() {
        val intent = Intent(this, PaymentConfirmationActivity::class.java).apply {
            putExtra("reservationId", "RES-${System.currentTimeMillis()}")
            putExtra("transactionId", "TXN-${System.currentTimeMillis()}")
            putExtra("offerName", offerName)
            putExtra("offerType", offerType)
            putExtra("totalPrice", offerPrice)
            putExtra("paymentMethod", "CARD")
            
            // Passer tous les détails selon le type
            when (offerType.lowercase()) {
                "circuit" -> {
                    putExtra("adults", intent.getIntExtra("adults", 0))
                    putExtra("childrenAges", intent.getIntegerArrayListExtra("childrenAges"))
                    putExtra("hotelLevel", intent.getStringExtra("hotelLevel"))
                    putExtra("flightClass", intent.getStringExtra("flightClass"))
                    putExtra("departureDate", intent.getLongExtra("departureDate", 0L))
                    putExtra("circuitDuration", intent.getIntExtra("circuitDuration", 0))
                    putExtra("circuitDestination", intent.getStringExtra("circuitDestination"))
                    
                    // Détail des prix
                    putExtra("basePrice", intent.getDoubleExtra("basePrice", 0.0))
                    putExtra("hotelSupplement", intent.getDoubleExtra("hotelSupplement", 0.0))
                    putExtra("flightSupplement", intent.getDoubleExtra("flightSupplement", 0.0))
                    putExtra("activitiesTotal", intent.getDoubleExtra("activitiesTotal", 0.0))
                    putExtra("selectedActivities", intent.getStringExtra("selectedActivities"))
                }
                "hotel" -> {
                    putExtra("checkInDate", checkInDate)
                    putExtra("checkOutDate", checkOutDate)
                    putExtra("numberOfNights", numberOfNights)
                    putExtra("numberOfAdults", numberOfAdults)
                    putExtra("numberOfChildren", numberOfChildren)
                    putExtra("roomType", roomType)
                    putExtra("viewType", viewType)
                    putExtra("mealPlan", mealPlan)
                }
                "flight" -> {
                    putExtra("departureDate", departureDate)
                    putExtra("returnDate", returnDate)
                    putExtra("adultsCount", adultsCount)
                    putExtra("childrenCount", childrenCount)
                    putExtra("flightClass", flightClass)
                }
            }
        }
        startActivity(intent)
        finish()
    }

    private fun showSuccessDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("✅ Paiement réussi!")
            .setMessage("Votre réservation est confirmée.")
            .setPositiveButton("Voir mes réservations") { _, _ ->
                navigateToHistory()
            }
            .setNegativeButton("Retour à l'accueil") { _, _ ->
                navigateToHome()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToHistory() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "history")
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }
}
