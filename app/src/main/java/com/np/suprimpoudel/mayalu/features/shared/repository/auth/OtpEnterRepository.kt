package com.np.suprimpoudel.mayalu.features.shared.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant
import javax.inject.Inject
import javax.inject.Named

class OtpEnterRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @Named("userDatabase")
    private val userDatabase: DatabaseReference
) {
    fun verifyUser(userId: String) =
        userDatabase.child("$userId/${FirebaseConstant.IS_VERIFIED}").setValue(true)
}