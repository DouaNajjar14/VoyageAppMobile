package com.example.voyageproject.ui.reset

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.databinding.ActivityResetPasswordBinding
import com.example.voyageproject.repository.AuthRepository
import com.example.voyageproject.ui.login.LoginActivity
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private val repo = AuthRepository()
    private var token: String? = null

    private var isNewPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        token = intent?.data?.getQueryParameter("token")
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Lien invalide", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupPasswordVisibility()
        setupValidationListener()

        binding.btnResetPassword.setOnClickListener {
            resetPassword()
        }
    }

    private fun setupPasswordVisibility() {

        binding.ivToggleNewPassword.setOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible
            togglePasswordVisibility(binding.etNewPassword, binding.ivToggleNewPassword, isNewPasswordVisible)
        }


        binding.ivToggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(binding.etConfirmPassword, binding.ivToggleConfirmPassword, isConfirmPasswordVisible)
        }
    }

    private fun togglePasswordVisibility(field: android.widget.EditText, icon: android.widget.ImageView, show: Boolean) {
        if (show) {
            field.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            icon.setImageResource(com.example.voyageproject.R.drawable.ic_eye)
        } else {
            field.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            icon.setImageResource(com.example.voyageproject.R.drawable.ic_eye_off)
        }
        field.setSelection(field.text.length)
    }

    private fun setupValidationListener() {
        binding.etNewPassword.setOnFocusChangeListener { _, hasFocus ->
            binding.tvPasswordHelp.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }

        binding.etNewPassword.addTextChangedListener {
            binding.tvPasswordHelp.visibility = View.GONE
        }
    }

    private fun resetPassword() {
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isStrongPassword(newPassword)) {
            Toast.makeText(this, "Le mot de passe n'est pas assez fort", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnResetPassword.isEnabled = false

        lifecycleScope.launch {
            try {
                val res = repo.resetPassword(token!!, newPassword)

                if (res.isSuccessful) {
                    Toast.makeText(this@ResetPasswordActivity, "Mot de passe réinitialisé avec succès", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Erreur : ${res.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@ResetPasswordActivity, "Erreur réseau : ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnResetPassword.isEnabled = true
            }
        }
    }

    private fun isStrongPassword(password: String): Boolean {
        val pattern = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=!]).{8,}$")
        return pattern.matches(password)
    }
}
