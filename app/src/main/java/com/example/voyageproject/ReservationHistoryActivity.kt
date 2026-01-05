package com.example.voyageproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.voyageproject.ui.history.adapters.ReservationAdapter
import com.example.voyageproject.model.Reservation
import com.example.voyageproject.network.RetrofitClient
import com.example.voyageproject.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservationHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutLoading: View
    private lateinit var layoutEmpty: View
    private lateinit var adapter: ReservationAdapter
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_history)

        // Initialiser SessionManager
        session = SessionManager(this)

        // Initialiser les vues
        recyclerView = findViewById(R.id.recyclerViewReservations)
        layoutLoading = findViewById(R.id.layoutLoading)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        // Configurer le RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Charger les réservations
        loadReservations()

        // Bouton retour
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }
    }

    private fun loadReservations() {
        val email = session.getEmail()

        if (email == null) {
            Toast.makeText(this, "Erreur: Email non trouvé", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        layoutLoading.visibility = View.VISIBLE
        layoutEmpty.visibility = View.GONE
        recyclerView.visibility = View.GONE

        RetrofitClient.api.getReservations(email).enqueue(object : Callback<List<Reservation>> {
            override fun onResponse(call: Call<List<Reservation>>, response: Response<List<Reservation>>) {
                layoutLoading.visibility = View.GONE

                if (response.isSuccessful) {
                    val reservations = response.body() ?: emptyList()
                    Log.d("ReservationHistory", "✅ ${reservations.size} réservation(s) chargée(s)")

                    if (reservations.isEmpty()) {
                        layoutEmpty.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        layoutEmpty.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter = ReservationAdapter(reservations) { reservation ->
                            // Ouvrir la page de détails
                            val intent = android.content.Intent(
                                this@ReservationHistoryActivity,
                                ReservationDetailActivity::class.java
                            )
                            intent.putExtra("reservation", reservation)
                            startActivity(intent)
                        }
                        recyclerView.adapter = adapter
                    }
                } else {
                    Log.e("ReservationHistory", "❌ Erreur: ${response.code()}")
                    layoutEmpty.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    Toast.makeText(this@ReservationHistoryActivity, 
                        "Erreur: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {
                layoutLoading.visibility = View.GONE
                layoutEmpty.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                Log.e("ReservationHistory", "❌ Erreur réseau: ${t.message}")
                Toast.makeText(this@ReservationHistoryActivity, 
                    "Erreur: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
