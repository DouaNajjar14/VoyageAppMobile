package com.example.voyageproject.utils

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveEmail(email: String) {
        prefs.edit().putString("email", email).apply()
    }

    fun getEmail(): String? = prefs.getString("email", null)

    fun clear() {
        prefs.edit().clear().apply()
    }
}