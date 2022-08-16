package com.np.suprimpoudel.mayalu.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant

class FirebaseService {
    companion object {
        private var firebaseAuth: FirebaseAuth? = null
        private var firebaseDatabase: FirebaseDatabase? = null
        private var firebaseStorage: FirebaseStorage? = null

        fun getFirebaseAuthInstance(): FirebaseAuth {
            return firebaseAuth ?: FirebaseAuth.getInstance()

        }

        fun getFirebaseDatabaseReference(): FirebaseDatabase {
            return firebaseDatabase
                ?: FirebaseDatabase.getInstance(FirebaseConstant.FIREBASE_DATABASE_REFERENCE)
        }

        fun getFirebaseStorageReference(): FirebaseStorage {
            return firebaseStorage ?: FirebaseStorage.getInstance()
        }
    }
}