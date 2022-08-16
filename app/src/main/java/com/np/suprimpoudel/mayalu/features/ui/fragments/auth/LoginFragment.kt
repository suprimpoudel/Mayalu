package com.np.suprimpoudel.mayalu.features.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentLoginBinding
import com.np.suprimpoudel.mayalu.features.shared.helper.EmailTemplate
import com.np.suprimpoudel.mayalu.features.shared.model.request.LoginRequest
import com.np.suprimpoudel.mayalu.features.ui.activity.UserActivity
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth.LoginViewModel
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth.SharedViewModel
import com.np.suprimpoudel.mayalu.utils.constants.Constants
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant
import com.np.suprimpoudel.mayalu.utils.extensions.hideKeyboard
import com.np.suprimpoudel.mayalu.utils.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    var loginRequest = LoginRequest()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_login,
            container, false
        )
        binding.btnLogin.txvButtonShared.text = getString(R.string.login)
        binding.loginRequest = loginRequest
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.txtSignUpBtn.id -> navigateTo(R.id.action_loginFragment_to_signUpFragment)
            binding.txtForgetPasswordBtn.id -> navigateTo(R.id.action_loginFragment_to_resetPasswordFragment)
            binding.btnLogin.mcvBtnShared.id -> loginIntoAccount()
        }
    }

    private fun initListeners() {
        binding.txtSignUpBtn.setOnClickListener(this)
        binding.txtForgetPasswordBtn.setOnClickListener(this)
        binding.btnLogin.mcvBtnShared.setOnClickListener(this)
    }

    private fun loginIntoAccount() {
        view?.hideKeyboard()
        binding.tIlEmailLogin.clearFocus()
        binding.tIlPasswordLogin.clearFocus()

        if (!isValid()) {
            return
        }
        binding.btnLogin.showProgress()
        loginViewModel.loginUser(loginRequest.email, loginRequest.password)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    checkIfDataUploaded(sharedViewModel.getUserId())
                } else {
                    binding.btnLogin.hideProgress()
                }
            }
            ?.addOnFailureListener {
                binding.btnLogin.hideProgress()
                showToast(it.message)
            }
    }

    private fun isValid(): Boolean {
        val email = loginRequest.email?.trim()
        if (loginRequest.email.isNullOrEmpty()) {
            binding.tIlEmailLogin.setCustomError("Please enter a value")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email ?: "").matches()) {
            binding.tIlEmailLogin.setCustomError("Invalid email address")
            return false
        } else binding.tIlEmailLogin.removeError()

        if (loginRequest.password.isNullOrEmpty()) {
            binding.tIlPasswordLogin.setCustomError("Please enter a value")
            return false
        } else binding.tIlPasswordLogin.removeError()
        return true
    }

    private fun checkIfDataUploaded(userId: String) {
        loginViewModel.checkIfUserHasUploadedData(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val isUserIsVerified: Boolean =
                            snapshot.child(userId)
                                .child(FirebaseConstant.IS_VERIFIED).value as Boolean
                        if (isUserIsVerified) {
                            if (snapshot.child(userId).child("uid").exists()) {
                                binding.btnLogin.hideProgress()
                                startActivity(Intent(context, UserActivity::class.java))
                                activity?.finish()
                            } else {
                                binding.btnLogin.hideProgress()
                                navigateTo(R.id.action_loginFragment_to_signUpFormFragment)
                            }
                        } else {
                            callSendGridApi(loginRequest.email ?: "", userId)
                        }
                    } else {
                        binding.btnLogin.hideProgress()
                        showToast("We couldn't find you")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast(error.message)
                }
            })

    }

    private fun callSendGridApi(recipientMail: String, userId: String) {
        val otpCode = (Math.random() * 900000).toInt() + 100000
        val response =
            sendMail(
                recipientMail,
                Constants.VERIFY_MAIL_SUBJECT,
                EmailTemplate.getWelcomeUserTemplate(otpCode.toString())
            )


        if (response.isSuccessful) {
            binding.btnLogin.hideProgress()
            val direction = LoginFragmentDirections.actionLoginFragmentToOTPEnterFragment(
                otpCode = otpCode,
                email = recipientMail,
                userId = userId
            )
            navigateWithDirections(direction)
            showSnackbar("Please make sure to check your spam folder too")

        } else {
            binding.btnLogin.hideProgress()
            showToast(response.errorMessage)
        }
    }
}