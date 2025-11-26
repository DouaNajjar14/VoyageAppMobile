package com.example.voyageproject.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.databinding.ActivityLoginBinding
import com.example.voyageproject.repository.AuthRepository
import com.example.voyageproject.ui.main.MainActivity
import com.example.voyageproject.ui.register.RegisterActivity
import com.example.voyageproject.ui.forgot.ForgotPasswordActivity
import com.example.voyageproject.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val repo = AuthRepository()
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+"
            if (!email.matches(emailPattern.toRegex())) {
                Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false
            binding.btnLogin.alpha = 0.6f

            lifecycleScope.launch {
                try {
                    val res = repo.login(email, password)

                    runOnUiThread {
                        if (res.isSuccessful && res.body() != null) {
                            // 🔥 Sauvegarde de l'email dans la session
                            session.saveEmail(email)

                            Toast.makeText(
                                this@LoginActivity,
                                "Bienvenue ${res.body()?.firstName ?: ""}",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            when (res.code()) {
                                200 -> {
                                    session.saveEmail(email)
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Bienvenue ${res.body()?.firstName}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                }
                                404 -> Toast.makeText(this@LoginActivity, "Aucun compte associé à cet email.", Toast.LENGTH_SHORT).show()
                                400 -> Toast.makeText(this@LoginActivity, "Email ou mot de passe incorrect.", Toast.LENGTH_SHORT).show()
                                403 -> Toast.makeText(this@LoginActivity, "Compte non activé. Vérifiez votre email.", Toast.LENGTH_LONG).show()
                                else -> Toast.makeText(this@LoginActivity, "Erreur : ${res.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Erreur réseau: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    runOnUiThread {
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.alpha = 1f
                    }
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}
