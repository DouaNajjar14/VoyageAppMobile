package com.example.voyageproject.ui.forgot

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.databinding.ActivityForgotPasswordBinding
import com.example.voyageproject.repository.AuthRepository
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val repo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendReset.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir votre email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSendReset.isEnabled = false

            lifecycleScope.launch {
                try {
                    val res = repo.forgot(email)
                    runOnUiThread {
                        if (res.isSuccessful) {
                            val message = res.body()?.get("message") ?: "Email envoyé"
                            Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_LONG).show()
                        } else {
                            val error = res.errorBody()?.string() ?: "Erreur inconnue"
                            Toast.makeText(this@ForgotPasswordActivity, error, Toast.LENGTH_SHORT).show()
                        }


                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@ForgotPasswordActivity, "Erreur réseau : ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    runOnUiThread { binding.btnSendReset.isEnabled = true }
                }
            }
        }

    }}