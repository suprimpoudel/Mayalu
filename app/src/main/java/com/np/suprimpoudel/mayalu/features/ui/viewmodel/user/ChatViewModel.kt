package com.np.suprimpoudel.mayalu.features.ui.viewmodel.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.np.suprimpoudel.mayalu.features.shared.model.ChatData
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ChatViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    @Named("chatDatabase")
    val chatDatabase: DatabaseReference,
    private val firebaseStorage: FirebaseStorage,
) : ViewModel() {
    private val _photoUploadState = MutableLiveData<Resource<ChatData>>()
    val photoUploadState = _photoUploadState

    fun uploadToBucket(photoByteArray: ByteArray, fileName: String?) {
        _photoUploadState.postValue(Resource.Loading())
        val reference = firebaseStorage.getReference(fileName ?: "")
        reference
            .putBytes(photoByteArray)
            .addOnSuccessListener {
                reference.downloadUrl
                    .addOnSuccessListener { uri ->
                        val chatData = ChatData(pictureUrl = uri.toString())
                        _photoUploadState.postValue(Resource.Success(chatData))
                    }
                    .addOnFailureListener { e ->
                        _photoUploadState.postValue(Resource.Error(e.message
                            ?: "Some error occurred"))
                    }
            }
            .addOnFailureListener { e ->
                _photoUploadState.postValue(Resource.Error(e.message ?: "Some error occurred"))
            }
    }

    fun deleteFromBucket(fileName: String) {
        firebaseStorage.getReference(fileName).delete()
    }
}