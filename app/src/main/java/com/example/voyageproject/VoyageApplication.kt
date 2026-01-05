package com.example.voyageproject

import android.app.Application
import com.example.voyageproject.network.RetrofitClient

class VoyageApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialiser RetrofitClient avec le context
        RetrofitClient.init(this)
    }
}
