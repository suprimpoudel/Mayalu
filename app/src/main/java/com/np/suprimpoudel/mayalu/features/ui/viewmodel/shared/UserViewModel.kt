package com.np.suprimpoudel.mayalu.features.ui.viewmodel.shared

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.np.suprimpoudel.mayalu.features.application.MyApplication
import com.np.suprimpoudel.mayalu.features.shared.model.User
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.base.BaseViewModel
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class UserViewModel @Inject constructor(
    application: MyApplication,
    private val firebaseAuth: FirebaseAuth,
    @Named("userDatabase")
    private val userDatabase: DatabaseReference,
    private val firebaseStorage: FirebaseStorage,
) : BaseViewModel(application) {
    companion object {
        const val TAG = "DEBUG_DATA"
    }

    private val _userState = MutableStateFlow<Resource<User>>(Resource.Empty())
    val userState = _userState

    private val _bucketState = MutableStateFlow<Resource<User>>(Resource.Empty())
    val bucketState = _bucketState

    fun getUserInfo() = viewModelScope.launch {
        val userId = firebaseAuth.uid
        userId?.let {
            _userState.value = Resource.Loading()
            try {
                if (context.hasInternetConnection()) {
                    userDatabase.child(userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.getValue(User::class.java)
                                user?.let {
                                    _userState.value = Resource.Success(
                                        data = it
                                    )
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                _userState.value = Resource.Error(error.message)
                            }
                        })
                } else {
                    _userState.value = Resource.Error("No Internet Connection")
                }
            } catch (ex: Exception) {
                _userState.value = Resource.Error(message = ex.message ?: "Some Error Occurred")
            }
        }
    }

    fun uploadImageToFirebaseStorage(data: Uri) {
        val userId = firebaseAuth.uid
        userId?.let {
            _bucketState.value = Resource.Loading()
            val reference =
                firebaseStorage.getReference(FirebaseConstant.DATA_PROFILE_PICTURE)
                    .child(userId)
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                    context.applicationContext?.contentResolver,
                    data
                )
            } catch (e: Exception) {
                Log.d(TAG, "uploadImageToFirebaseStorage: ${e.message}")
                _bucketState.value = Resource.Error(e.message ?: "Some Error Occurred")
            }
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 20, baos)
            val bitmapData = baos.toByteArray()
            reference.putBytes(bitmapData)
                .addOnSuccessListener { _ ->
                    reference.downloadUrl
                        .addOnSuccessListener { url ->
                            userDatabase.child("${firebaseAuth.uid}/profilePhotoUrl")
                                .setValue(url.toString())
                                .addOnCompleteListener { event ->
                                    if (event.isSuccessful) {
                                        _bucketState.value = Resource.Success(User())
                                    }
                                }
                        }
                        .addOnFailureListener { ex ->
                            _bucketState.value =
                                Resource.Error(ex.message ?: "Some Error Occurred")
                        }
                }
                .addOnFailureListener {
                    _bucketState.value =
                        Resource.Error(it.message ?: "Some Error Occurred")
                }
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        val user = firebaseAuth.currentUser
        user?.let {
            val userEmail = user.email
            userEmail?.let { email ->
                val credential = EmailAuthProvider.getCredential(email, currentPassword)

                user.reauthenticate(credential)
                    .addOnCompleteListener { event ->
                        if (event.isSuccessful) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener {
                                    Log.d(TAG, "updatePassword: Success")
                                    _userState.value = Resource.Success(
                                        User()
                                    )
                                }
                                .addOnFailureListener {
                                    _userState.value = Resource.Error(
                                        it.message ?: "Some Error Occurred"
                                    )
                                }
                        }
                    }
                    .addOnFailureListener {
                        _userState.value = Resource.Error(
                            it.message ?: "Some Error Occurred"
                        )
                    }
            }
        }
    }

    fun registerDeviceInRealtimeDatabase(token: String) {
        val userId = firebaseAuth.uid
        userId?.let {
            userDatabase.child("$userId/token").setValue(token)
                .addOnFailureListener {
                    Log.d(TAG, "registerDeviceInRealtimeDatabase: ${it.message}")
                }
                .addOnSuccessListener {
                    Log.d(TAG, "registerDeviceInRealtimeDatabase: Registered Device")
                }
        }
    }

    fun logout() = viewModelScope.launch {
        firebaseAuth.signOut()
    }
}