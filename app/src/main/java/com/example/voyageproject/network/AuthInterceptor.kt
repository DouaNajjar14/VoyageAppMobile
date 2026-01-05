package com.example.voyageproject.network

import android.content.Context
import android.util.Log
import com.example.voyageproject.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()
        
        // Liste des endpoints qui NE nécessitent PAS d'authentification
        val publicEndpoints = listOf(
            "/api/client/register",
            "/api/client/login",
            "/api/client/confirm",
            "/api/client/forgot-password",
            "/api/client/reset-password",
            "/api/hotels",
            "/api/circuits",
            "/api/flights"
        )
        
        // Si c'est un endpoint public, ne pas ajouter l'email
        val isPublic = publicEndpoints.any { url.contains(it) }
        
        if (isPublic) {
            Log.d("AUTH", "📖 Endpoint public: $url - pas d'auth")
            return chain.proceed(originalRequest)
        }
        
        // Pour les autres endpoints, ajouter l'email
        val session = SessionManager(context)
        val email = session.getEmail()
        
        if (email.isNullOrEmpty()) {
            Log.w("AUTH", "⚠️ Pas d'email pour endpoint privé: $url")
            return chain.proceed(originalRequest)
        }
        
        // Ajouter l'email comme paramètre de requête
        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter("email", email)
            .build()
        
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()
        
        Log.d("AUTH", "✅ Email ajouté pour: $url")
        
        return chain.proceed(newRequest)
    }
}
