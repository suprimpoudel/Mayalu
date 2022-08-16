package com.np.suprimpoudel.mayalu.features.shared.repository.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ResetPasswordRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun checkIfUserExists(email: String) = firebaseAuth.fetchSignInMethodsForEmail(email)

    fun sendPasswordResetLink(email: String) = firebaseAuth.sendPasswordResetEmail(email)
}