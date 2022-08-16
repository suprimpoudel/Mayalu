package com.np.suprimpoudel.mayalu.features.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentResetPasswordBinding
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth.ResetPasswordViewModel
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth.SharedViewModel
import com.np.suprimpoudel.mayalu.utils.extensions.hideKeyboard
import com.np.suprimpoudel.mayalu.utils.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    private var binding: FragmentResetPasswordBinding? = null
    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_reset_password,
            container, false
        )
        binding?.btnResetPassword?.txvButtonShared?.text = getString(R.string.continue_text)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        binding?.imgBackIconForgotPassword?.setOnClickListener {
            popBack()
        }
        binding?.btnResetPassword?.mcvBtnShared?.setOnClickListener {
            view?.hideKeyboard()
            binding?.tilEmailReset?.clearFocus()

            sendMail()
        }

    }

    private fun sendMail() {
        val recipient = binding?.edtEmailReset?.text.toString().trim()

        if (validateMail(recipient)) {
            binding?.btnResetPassword?.showProgress()
            checkIfUserExists(recipient)
        }
    }

    private fun checkIfUserExists(recipient: String) {
        resetPasswordViewModel.checkIfUserExists(recipient)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    binding?.btnResetPassword?.hideProgress()
                    showToast(task.exception?.message)
                } else {
                    if (task.result.signInMethods?.isEmpty() == true) {
                        showToast("User doesn't Exists")
                    } else {
                        resetPasswordViewModel.sendPasswordResetLink(recipient)
                            .addOnCompleteListener { sendResetPasswordTask ->
                                binding?.btnResetPassword?.hideProgress()
                                if (sendResetPasswordTask.isSuccessful) {
                                    popBack()
                                    showSnackbar("Password Reset Link Sent Successfully")
                                } else {
                                    showToast(sendResetPasswordTask.exception?.localizedMessage)
                                }
                            }
                    }
                }
            }
            .addOnFailureListener {
                binding?.btnResetPassword?.hideProgress()
                showToast(it.message)
            }
    }

    private fun validateMail(recipient: String): Boolean {
        if (recipient.isEmpty()) {
            binding?.tilEmailReset?.setCustomError(getString(R.string.field_cannot_be_empty))
            return false
        } else binding?.tilEmailReset?.removeError()
        return true
    }

    override fun onDestroyView() {
        sharedViewModel.signOut()
        super.onDestroyView()
    }
}