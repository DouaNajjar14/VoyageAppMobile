package com.example.voyageproject.ui.register

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.databinding.ActivityRegisterBinding
import com.example.voyageproject.model.RegisterRequest
import com.example.voyageproject.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val repo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tvPasswordHelp.visibility = android.view.View.VISIBLE
            } else {
                binding.tvPasswordHelp.visibility = android.view.View.GONE
            }
        }

        binding.btnRegister.setOnClickListener {
            val first = binding.etFirstName.text.toString().trim()
            val last = binding.etLastName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val telephone=binding.etPhone.text.toString().trim()
            val pass = binding.etPassword.text.toString()
            val confirm = binding.etConfirm.text.toString()


            if (first.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty() || telephone.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!]).{8,}\$")
            if (!pass.matches(passwordRegex)) {
                Toast.makeText(
                    this,
                    "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un symbole",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }


            if (pass != confirm) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            lifecycleScope.launch {
                try {
                    val res = repo.register(RegisterRequest(email, pass, first, lastName = last, telephone = telephone ))
                    runOnUiThread {
                        if (res.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Inscription réussie. Vérifiez votre email.",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Erreur: ${res.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Erreur réseau: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    }
