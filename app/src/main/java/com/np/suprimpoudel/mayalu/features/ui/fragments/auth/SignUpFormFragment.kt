package com.np.suprimpoudel.mayalu.features.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentSignUpGenderPreferenceBinding
import com.np.suprimpoudel.mayalu.utils.util.navigateWithDirections
import com.np.suprimpoudel.mayalu.utils.util.showToast

class SignUpFormFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSignUpGenderPreferenceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_sign_up_gender_preference,
            container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.btnContinue.id -> validateOptions()
        }
    }

    private fun initListener() {
        binding.btnContinue.setOnClickListener(this)
    }

    private fun validateOptions() {
        val genderSelected = binding.radioGender.checkedRadioButtonId
        if (genderSelected == -1) {
            showToast("Please select your gender")
            return
        }
        val lookingFor = binding.radioInterested.checkedRadioButtonId
        if (lookingFor == -1) {
            showToast("Please select your interest")
            return
        }

        getData()
    }

    private fun getData() {
        val gender = when (binding.radioGender.checkedRadioButtonId) {
            binding.radioBtnMale.id -> "Male"
            else -> "Female"
        }
        val lookingFor = when (binding.radioInterested.checkedRadioButtonId) {
            binding.radioBtnMaleInterested.id -> "Male"
            binding.radioBtnFemaleInterested.id -> "Female"
            else -> "Both"
        }
        val direction = SignUpFormFragmentDirections.actionSignUpFormFragmentToSignUpPersonalDetailFragment(
            gender = gender,
            lookingFor = lookingFor
        )
        navigateWithDirections(direction)
    }
}