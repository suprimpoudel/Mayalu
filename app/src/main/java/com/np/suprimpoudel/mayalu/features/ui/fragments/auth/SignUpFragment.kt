package com.np.suprimpoudel.mayalu.features.ui.fragments.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentSignUpBinding
import com.np.suprimpoudel.mayalu.features.shared.model.request.LoginRequest
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth.SharedViewModel
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth.SignUpViewModel
import com.np.suprimpoudel.mayalu.utils.extensions.hideKeyboard
import com.np.suprimpoudel.mayalu.utils.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(), View.OnClickListener {
    private var binding: FragmentSignUpBinding? = null
    private val sharedViewModel: SharedViewModel by viewModels()
    private val viewModel: SignUpViewModel by viewModels()

    private var signUpRequest = LoginRequest()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_sign_up,
            container, false
        )
        binding?.btnSignUp?.txvButtonShared?.text = getString(R.string.sign_up)
        binding?.signUpRequest = signUpRequest
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    override fun onDestroyView() {
        sharedViewModel.signOut()
        super.onDestroyView()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding?.imvBackIcon?.id -> popBack()
            binding?.txvLoginBtn?.id -> popBack()
            binding?.btnSignUp?.mcvBtnShared?.id -> signUpUser()
        }
    }

    private fun initListener() {
        binding?.imvBackIcon?.setOnClickListener(this)
        binding?.txvLoginBtn?.setOnClickListener(this)
        binding?.btnSignUp?.mcvBtnShared?.setOnClickListener(this)
    }

    private fun signUpUser() {
        view?.hideKeyboard()
        if (!isValid()) {
            return
        }
        binding?.btnSignUp?.showProgress()
        viewModel.createUser(signUpRequest.email, signUpRequest.password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    createUserInFirebaseDatabase()
                } else {
                    binding?.btnSignUp?.hideProgress()
                    showToast(task.exception?.localizedMessage)
                }
            }
    }

    private fun createUserInFirebaseDatabase() {
        viewModel.createUserReferenceInRealtimeDatabase(sharedViewModel.getUserId())
            .addOnCompleteListener { task ->
                binding?.btnSignUp?.hideProgress()
                if (task.isSuccessful) {
                    popBack()
                    showToast("User Created")
                } else {
                    showToast(task.exception?.localizedMessage)
                }
            }
    }

    private fun isValid(): Boolean {
        val email = signUpRequest.email?.trim()
        if (email.isNullOrEmpty()) {
            binding?.tilEmail?.setCustomError("Please enter a value")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding?.tilEmail?.setCustomError("Invalid email address")
            return false
        } else binding?.tilEmail?.removeError()

        if (signUpRequest.password.isNullOrEmpty()) {
            binding?.tilPasswordSignUp?.setCustomError("Please enter a value")
            return false
        } else binding?.tilPasswordSignUp?.removeError()
        return true
    }
}