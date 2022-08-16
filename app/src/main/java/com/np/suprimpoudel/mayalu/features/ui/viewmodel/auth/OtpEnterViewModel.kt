package com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.np.suprimpoudel.mayalu.features.shared.repository.auth.OtpEnterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OtpEnterViewModel @Inject constructor(
    private val otpEnterRepository: OtpEnterRepository
): ViewModel() {
    fun verifyUser(userId: String) = otpEnterRepository.verifyUser(userId)
}