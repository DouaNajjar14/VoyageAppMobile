package com.example.voyageproject.ui.hotel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.voyageproject.databinding.ActivityRoomSelectionBinding
import com.example.voyageproject.model.*
import com.example.voyageproject.ui.hotel.adapter.RoomOptionAdapter

class RoomSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomSelectionBinding
    private lateinit var adapter: RoomOptionAdapter
    
    private var hotel: Hotel? = null
    private var checkInDate: String = ""
    private var checkOutDate: String = ""
    private var numberOfNights: Int = 0
    private var adultsCount: Int = 0
    private val children = mutableListOf<Child>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        getIntentData()
        setupRecyclerView()
        loadRoomOptions()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Choisir une chambre"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun getIntentData() {
        hotel = intent.getParcelableExtra("hotel")
        checkInDate = intent.getStringExtra("checkInDate") ?: ""
        checkOutDate = intent.getStringExtra("checkOutDate") ?: ""
        numberOfNights = intent.getIntExtra("numberOfNights", 1)
        adultsCount = intent.getIntExtra("adultsCount", 2)
        
        val childrenList = intent.getParcelableArrayListExtra<Child>("children")
        if (childrenList != null) {
            children.addAll(childrenList)
        }

        // Afficher les infos de recherche
        val childrenText = if (children.isNotEmpty()) ", ${children.size} enfant${if (children.size > 1) "s" else ""}" else ""
        binding.tvSearchInfo.text = "$checkInDate - $checkOutDate • $adultsCount adulte${if (adultsCount > 1) "s" else ""}$childrenText"
    }

    private fun setupRecyclerView() {
        adapter = RoomOptionAdapter(
            numberOfNights = numberOfNights,
            adultsCount = adultsCount,
            children = children,
            onRoomSelected = { roomOption, selectedView, selectedMeal, totalPrice ->
                navigateToSummary(roomOption, selectedView, selectedMeal, totalPrice)
            }
        )

        binding.rvRoomOptions.layoutManager = LinearLayoutManager(this)
        binding.rvRoomOptions.adapter = adapter
    }

    private fun loadRoomOptions() {
        // Créer des options de chambres de démonstration
        // En production, ces données viendraient du backend
        val roomOptions = createDemoRoomOptions()

        if (roomOptions.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvRoomOptions.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvRoomOptions.visibility = View.VISIBLE
            adapter.submitList(roomOptions)
        }
    }

    private fun createDemoRoomOptions(): List<RoomOption> {
        val hotelId = hotel?.id ?: return emptyList()

        return listOf(
            RoomOption(
                id = "room1",
                hotelId = hotelId,
                roomType = "Chambre Standard",
                capacity = 2,
                bedType = "1 lit double",
                basePrice = 80.0,
                imageUrl = hotel?.imageUrl ?: "",
                description = "Chambre confortable avec toutes les commodités",
                amenities = listOf("WiFi gratuit", "TV écran plat", "Climatisation", "Salle de bain privée"),
                viewOptions = listOf(
                    ViewOption("garden", "Vue jardin", 0.0),
                    ViewOption("sea", "Vue mer", 20.0),
                    ViewOption("pool", "Vue piscine", 10.0)
                ),
                mealOptions = listOf(
                    MealOption("none", "Sans repas", 0.0),
                    MealOption("breakfast", "Petit déjeuner", 15.0),
                    MealOption("half_board", "Demi-pension", 35.0),
                    MealOption("all_inclusive", "All inclusive", 50.0)
                )
            ),
            RoomOption(
                id = "room2",
                hotelId = hotelId,
                roomType = "Chambre Deluxe",
                capacity = 3,
                bedType = "1 lit double + 1 lit simple",
                basePrice = 120.0,
                imageUrl = hotel?.imageUrl ?: "",
                description = "Chambre spacieuse avec vue mer",
                amenities = listOf("WiFi gratuit", "TV écran plat", "Climatisation", "Mini-bar", "Balcon"),
                viewOptions = listOf(
                    ViewOption("sea", "Vue mer", 0.0)
                ),
                mealOptions = listOf(
                    MealOption("none", "Sans repas", 0.0),
                    MealOption("breakfast", "Petit déjeuner", 15.0),
                    MealOption("half_board", "Demi-pension", 35.0),
                    MealOption("all_inclusive", "All inclusive", 50.0)
                )
            ),
            RoomOption(
                id = "room3",
                hotelId = hotelId,
                roomType = "Suite",
                capacity = 4,
                bedType = "1 lit king-size + canapé-lit",
                basePrice = 200.0,
                imageUrl = hotel?.imageUrl ?: "",
                description = "Suite luxueuse avec salon séparé",
                amenities = listOf("WiFi gratuit", "TV écran plat", "Climatisation", "Mini-bar", "Balcon", "Jacuzzi"),
                viewOptions = listOf(
                    ViewOption("sea", "Vue mer panoramique", 0.0)
                ),
                mealOptions = listOf(
                    MealOption("none", "Sans repas", 0.0),
                    MealOption("breakfast", "Petit déjeuner", 15.0),
                    MealOption("half_board", "Demi-pension", 35.0),
                    MealOption("all_inclusive", "All inclusive", 50.0)
                )
            ),
            RoomOption(
                id = "room4",
                hotelId = hotelId,
                roomType = "Chambre Familiale",
                capacity = 5,
                bedType = "2 lits doubles",
                basePrice = 150.0,
                imageUrl = hotel?.imageUrl ?: "",
                description = "Chambre spacieuse parfaite pour les familles",
                amenities = listOf("WiFi gratuit", "TV écran plat", "Climatisation", "Réfrigérateur", "Espace jeux"),
                viewOptions = listOf(
                    ViewOption("garden", "Vue jardin", 0.0),
                    ViewOption("pool", "Vue piscine", 15.0)
                ),
                mealOptions = listOf(
                    MealOption("none", "Sans repas", 0.0),
                    MealOption("breakfast", "Petit déjeuner", 15.0),
                    MealOption("half_board", "Demi-pension", 35.0),
                    MealOption("all_inclusive", "All inclusive", 50.0)
                )
            )
        )
    }

    private fun navigateToSummary(
        roomOption: RoomOption,
        selectedView: ViewOption,
        selectedMeal: MealOption,
        totalPrice: Double
    ) {
        val bookingDetails = BookingDetails(
            hotelId = hotel?.id ?: "",
            hotelName = hotel?.name ?: "",
            hotelCity = hotel?.city ?: "",
            roomType = roomOption.roomType,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            numberOfNights = numberOfNights,
            numberOfAdults = adultsCount,
            numberOfChildren = children.size,
            viewType = selectedView.type,
            viewLabel = selectedView.label,
            mealPlan = selectedMeal.type,
            mealLabel = selectedMeal.label,
            basePrice = roomOption.basePrice * numberOfNights,
            viewPrice = selectedView.pricePerNight * numberOfNights,
            mealPrice = selectedMeal.pricePerNight * numberOfNights,
            totalPrice = totalPrice
        )

        Log.d("ROOM_SELECTION", "========================================")
        Log.d("ROOM_SELECTION", "Création BookingDetails:")
        Log.d("ROOM_SELECTION", "  Hotel ID: ${bookingDetails.hotelId}")
        Log.d("ROOM_SELECTION", "  Hotel Name: ${bookingDetails.hotelName}")
        Log.d("ROOM_SELECTION", "  Room Type: ${bookingDetails.roomType}")
        Log.d("ROOM_SELECTION", "  Total Price: ${bookingDetails.totalPrice}")
        Log.d("ROOM_SELECTION", "  Adults: $adultsCount, Children: ${children.size}")
        Log.d("ROOM_SELECTION", "========================================")

        val intent = Intent(this, BookingSummaryDetailedActivity::class.java).apply {
            putExtra("bookingDetails", bookingDetails)
            putParcelableArrayListExtra("children", ArrayList(children))
        }
        startActivity(intent)
    }
}
