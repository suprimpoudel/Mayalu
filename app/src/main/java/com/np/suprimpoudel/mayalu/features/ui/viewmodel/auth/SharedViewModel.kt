package com.np.suprimpoudel.mayalu.features.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.np.suprimpoudel.mayalu.features.shared.repository.auth.SharedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val sharedRepository: SharedRepository
) : ViewModel() {
    fun getUserId() = sharedRepository.getUserId()

    fun signOut() = sharedRepository.signOut()
}