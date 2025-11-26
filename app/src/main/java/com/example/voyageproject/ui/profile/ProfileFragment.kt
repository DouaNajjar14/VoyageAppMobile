package com.example.voyageproject.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.voyageproject.databinding.FragmentProfileBinding
import com.example.voyageproject.model.UpdateProfileRequest
import com.example.voyageproject.repository.AuthRepository
import com.example.voyageproject.ui.login.LoginActivity
import com.example.voyageproject.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var session: SessionManager
    private val repo = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        session = SessionManager(requireContext())

        loadProfile()

        binding.btnUpdate.setOnClickListener { updateProfile() }
        binding.btnLogout.setOnClickListener { logout() }

        return binding.root
    }

    private fun loadProfile() {
        val email = session.getEmail() ?: return

        lifecycleScope.launch {
            try {
                val res = repo.getProfile(email)

                if (res.isSuccessful) {
                    val c = res.body()!!

                    binding.etFirstName.setText(c.firstName)
                    binding.etLastName.setText(c.lastName)
                    binding.etEmail.setText(c.email)
                    binding.etPhone.setText(c.telephone)

                } else {
                    Toast.makeText(requireContext(), "Impossible de charger le profil", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur réseau : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfile() {
        val oldEmail = session.getEmail() ?: return

        val req = UpdateProfileRequest(
            firstName = binding.etFirstName.text.toString().trim(),
            lastName = binding.etLastName.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            telephone = binding.etPhone.text.toString().trim()
        )

        lifecycleScope.launch {
            try {
                val res = repo.updateProfile(oldEmail, req)

                if (res.isSuccessful && res.body() != null) {
                    Toast.makeText(requireContext(), "Profil mis à jour", Toast.LENGTH_SHORT).show()
                    session.saveEmail(req.email) // mettre à jour email si modifié
                } else {
                    val code = res.code()
                    val msg = res.errorBody()?.string() ?: "Erreur inconnue"
                    Toast.makeText(requireContext(), "Erreur modification ($code): $msg", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erreur réseau : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun logout() {
        session.clear()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }
}
