package com.np.suprimpoudel.mayalu.features.shared.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant
import javax.inject.Inject
import javax.inject.Named

class LoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @Named("firebaseDatabase")
    private val firebaseDatabase: FirebaseDatabase
) {
    fun loginUser(email: String?, password: String?) =
        email?.let { emailNotNull ->
            password?.let { passwordNotNull ->
                firebaseAuth.signInWithEmailAndPassword(emailNotNull, passwordNotNull)
            }
        }

    fun checkIfUserHasUploadedData(userId: String): Query {
        val userRef =
            firebaseDatabase.getReference(FirebaseConstant.DATA_USERS)
        return userRef.orderByChild(userId)
    }
}