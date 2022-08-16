package com.np.suprimpoudel.mayalu.features.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentOTPEnterBinding
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth.OtpEnterViewModel
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth.SharedViewModel
import com.np.suprimpoudel.mayalu.utils.extensions.hideKeyboard
import com.np.suprimpoudel.mayalu.utils.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OTPEnterFragment : Fragment(), View.OnClickListener {
    private var binding: FragmentOTPEnterBinding? = null
    private val viewModel: OtpEnterViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private val args by navArgs<OTPEnterFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_o_t_p_enter,
            container, false
        )
        binding?.btnContinueResetPassword?.txvButtonShared?.text = getString(R.string.verify_mail)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding?.imgBackIconResetPassword?.id -> popBack()
            binding?.btnContinueResetPassword?.mcvBtnShared?.id -> changePassword()
        }
    }

    private fun initListener() {
        binding?.imgBackIconResetPassword?.setOnClickListener(this)
        binding?.btnContinueResetPassword?.mcvBtnShared?.setOnClickListener(this)
    }

    private fun changePassword() {
        view?.hideKeyboard()
        val pin = binding?.pinViewResetPassword?.text.toString().trim()

        if (pin.isNotEmpty()) {
            if (pin == args.otpCode.toString()) {
                binding?.btnContinueResetPassword?.showProgress()
                viewModel.verifyUser(sharedViewModel.getUserId())
                    .addOnCompleteListener { task ->
                        binding?.btnContinueResetPassword?.hideProgress()
                        if (task.isSuccessful) {
                            redirectToLoginScreen()
                        } else {
                            showToast(task.exception?.message)
                        }
                    }
            } else {
                showToast("Code not valid")
            }
        }
    }

    override fun onDestroyView() {
        sharedViewModel.signOut()
        super.onDestroyView()
    }

    private fun redirectToLoginScreen() {
        popBack()
        showSnackbar("OTP Verified Successfully")
    }
}