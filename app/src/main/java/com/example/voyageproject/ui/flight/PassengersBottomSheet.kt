package com.example.voyageproject.ui.flight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.voyageproject.R
import com.example.voyageproject.databinding.BottomSheetPassengersBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PassengersBottomSheet(
    private val initialAdults: Int,
    private val initialChildren: Int,
    private val initialBabies: Int,
    private val initialClass: String,
    private val onConfirm: (adults: Int, children: Int, babies: Int, flightClass: String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPassengersBinding? = null
    private val binding get() = _binding!!

    private var adultsCount = initialAdults
    private var childrenCount = initialChildren
    private var babiesCount = initialBabies
    private var selectedClass = initialClass

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPassengersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupInitialValues()
        setupAdultsCounter()
        setupChildrenCounter()
        setupBabiesCounter()
        setupClassSelection()
        setupConfirmButton()
    }

    private fun setupInitialValues() {
        binding.tvAdultsCount.text = adultsCount.toString()
        binding.tvChildrenCount.text = childrenCount.toString()
        binding.tvBabiesCount.text = babiesCount.toString()
        
        // Sélectionner la classe initiale
        when (selectedClass) {
            "Économique" -> binding.chipEconomy.isChecked = true
            "Premium Éco" -> binding.chipPremiumEconomy.isChecked = true
            "Affaires" -> binding.chipBusiness.isChecked = true
            "Première" -> binding.chipFirst.isChecked = true
        }
    }

    private fun setupAdultsCounter() {
        binding.btnPlusAdults.setOnClickListener {
            if (adultsCount + childrenCount + babiesCount < 9) {
                adultsCount++
                binding.tvAdultsCount.text = adultsCount.toString()
                updateButtonStates()
            }
        }

        binding.btnMinusAdults.setOnClickListener {
            if (adultsCount > 1) {
                adultsCount--
                binding.tvAdultsCount.text = adultsCount.toString()
                updateButtonStates()
            }
        }
    }

    private fun setupChildrenCounter() {
        binding.btnPlusChildren.setOnClickListener {
            if (adultsCount + childrenCount + babiesCount < 9) {
                childrenCount++
                binding.tvChildrenCount.text = childrenCount.toString()
                updateButtonStates()
            }
        }

        binding.btnMinusChildren.setOnClickListener {
            if (childrenCount > 0) {
                childrenCount--
                binding.tvChildrenCount.text = childrenCount.toString()
                updateButtonStates()
            }
        }
    }

    private fun setupBabiesCounter() {
        binding.btnPlusBabies.setOnClickListener {
            if (adultsCount + childrenCount + babiesCount < 9 && babiesCount < adultsCount) {
                babiesCount++
                binding.tvBabiesCount.text = babiesCount.toString()
                updateButtonStates()
            }
        }

        binding.btnMinusBabies.setOnClickListener {
            if (babiesCount > 0) {
                babiesCount--
                binding.tvBabiesCount.text = babiesCount.toString()
                updateButtonStates()
            }
        }
    }

    private fun updateButtonStates() {
        val totalPassengers = adultsCount + childrenCount + babiesCount
        
        // Désactiver les boutons + si limite atteinte
        binding.btnPlusAdults.isEnabled = totalPassengers < 9
        binding.btnPlusChildren.isEnabled = totalPassengers < 9
        binding.btnPlusBabies.isEnabled = totalPassengers < 9 && babiesCount < adultsCount
        
        // Désactiver le bouton - pour adultes si minimum atteint
        binding.btnMinusAdults.isEnabled = adultsCount > 1
        binding.btnMinusChildren.isEnabled = childrenCount > 0
        binding.btnMinusBabies.isEnabled = babiesCount > 0
    }

    private fun setupClassSelection() {
        binding.chipGroupClass.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedClass = when {
                checkedIds.contains(R.id.chipEconomy) -> "Économique"
                checkedIds.contains(R.id.chipPremiumEconomy) -> "Premium Éco"
                checkedIds.contains(R.id.chipBusiness) -> "Affaires"
                checkedIds.contains(R.id.chipFirst) -> "Première"
                else -> "Économique"
            }
        }
    }

    private fun setupConfirmButton() {
        binding.btnConfirm.setOnClickListener {
            onConfirm(adultsCount, childrenCount, babiesCount, selectedClass)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
