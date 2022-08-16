package com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.np.suprimpoudel.mayalu.features.shared.repository.auth.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
) : ViewModel() {
    fun loginUser(email: String?, password: String?) = loginRepository.loginUser(email, password)

    fun checkIfUserHasUploadedData(userId: String) =
        loginRepository.checkIfUserHasUploadedData(userId)
}