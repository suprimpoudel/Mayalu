package com.np.suprimpoudel.mayalu.features.ui.viewmodel.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.np.suprimpoudel.mayalu.features.shared.model.Post
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class PostViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @Named("userDatabase")
    val userDatabase: DatabaseReference,
    @Named("postDatabase")
    val postDatabase: DatabaseReference,
    val firebaseStorage: FirebaseStorage,
) : ViewModel() {
    val userId: String = firebaseAuth.currentUser?.uid ?: ""

    private val _postStatus = MutableLiveData<Resource<Post>>()
    val postStatus = _postStatus

    fun deletePost(post: Post) {
        _postStatus.value = Resource.Loading()
        postDatabase.child(post.postId ?: "").removeValue()
            .addOnSuccessListener {
                _postStatus.value = Resource.Success(post)
            }
            .addOnFailureListener {
                _postStatus.value = Resource.Error(it.message ?: "Some Error Occurred")
            }
    }

    fun savePost(post: Post) {
        _postStatus.value = Resource.Loading()
        val key: String? = if (post.postId == null) {
            postDatabase.child(userId).push().key
        } else {
            post.postId ?: ""
        }
        if (key?.isEmpty() == false) {
            postDatabase.child(key).setValue(post.copy(postId = key))
                .addOnSuccessListener {
                    _postStatus.value = Resource.Success(post)
                }
                .addOnFailureListener {
                    _postStatus.value = Resource.Error(it.message ?: "Some Error Occurred")
                }
        }
    }
}