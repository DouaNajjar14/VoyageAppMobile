package com.example.voyageproject.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.voyageproject.R
import com.example.voyageproject.databinding.ActivityMainBinding
import com.example.voyageproject.ui.home.HomeFragment
import com.example.voyageproject.ui.history.HistoryFragment
import com.example.voyageproject.ui.notifications.NotificationsFragment
import com.example.voyageproject.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Demander la permission pour les notifications (Android 13+)
        requestNotificationPermission()

        // Vérifier si on doit naviguer vers un fragment spécifique
        val navigateTo = intent.getStringExtra("navigate_to")
        
        // Afficher le fragment approprié
        if (savedInstanceState == null) {
            when (navigateTo) {
                "history" -> {
                    replaceFragment(HistoryFragment())
                    binding.bottomNavigationView.selectedItemId = R.id.nav_history
                }
                else -> {
                    replaceFragment(com.example.voyageproject.ui.home.HomeFragmentWithSearch())
                }
            }
        }

        // Configuration de la navigation
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(com.example.voyageproject.ui.home.HomeFragmentWithSearch())
                    true
                }
                R.id.nav_history -> {
                    replaceFragment(HistoryFragment())
                    true
                }
                R.id.nav_notifications -> {
                    replaceFragment(NotificationsFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }
}
