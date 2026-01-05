package com.example.voyageproject.utils

import android.content.Context
import android.util.Log

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveEmail(email: String) {
        prefs.edit().putString("email", email).apply()
        Log.d("SESSION", "✅ Email sauvegardé: $email")
    }

    fun getEmail(): String? {
        val email = prefs.getString("email", null)
        Log.d("SESSION", "📧 Email récupéré: ${email ?: "NULL"}")
        return email
    }
    
    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
        Log.d("SESSION", "✅ Token sauvegardé")
    }
    
    fun getToken(): String? {
        val token = prefs.getString("token", null)
        Log.d("SESSION", "🔑 Token récupéré: ${if (token != null) "EXISTS" else "NULL"}")
        return token
    }

    fun isLoggedIn(): Boolean {
        val loggedIn = !getEmail().isNullOrEmpty()
        Log.d("SESSION", "🔐 Utilisateur connecté: $loggedIn")
        return loggedIn
    }

    fun clear() {
        prefs.edit().clear().apply()
        Log.d("SESSION", "🗑️ Session effacée")
    }
}
