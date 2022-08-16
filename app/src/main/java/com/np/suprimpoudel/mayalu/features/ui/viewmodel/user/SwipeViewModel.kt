package com.np.suprimpoudel.mayalu.features.ui.viewmodel.user

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.np.suprimpoudel.mayalu.features.application.MyApplication
import com.np.suprimpoudel.mayalu.features.shared.model.User
import com.np.suprimpoudel.mayalu.features.shared.model.request.Notification
import com.np.suprimpoudel.mayalu.features.shared.model.request.NotificationRequest
import com.np.suprimpoudel.mayalu.features.shared.model.response.NotificationResponse
import com.np.suprimpoudel.mayalu.features.shared.repository.user.SwipeRepository
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.base.BaseViewModel
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_MATCHES
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_NAME
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_PROFILE_PICTURE
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_SWIPE_LEFT
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_SWIPE_RIGHT
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SwipeViewModel @Inject constructor(
    application: MyApplication,
    val firebaseAuth: FirebaseAuth,
    @Named("userDatabase")
    val userDatabase: DatabaseReference,
    @Named("chatDatabase")
    private val chatDatabase: DatabaseReference,
    private val swipeRepository: SwipeRepository,
) : BaseViewModel(application) {
    private val _notificationState = MutableLiveData<Resource<NotificationResponse>>()
    val notificationState = _notificationState

    fun sendNotificationToDevice(notificationRequest: NotificationRequest) = viewModelScope.launch {
        _notificationState.value = Resource.Loading()
        try {
            val response = swipeRepository.sendMatchedNotification(notificationRequest)
            if (response.isSuccessful) {
                _notificationState.value =
                    Resource.Success(response.body() ?: NotificationResponse())
            } else {
                _notificationState.value = Resource.Error(response.errorBody().toString())
            }
        } catch (ex: Exception) {
            _notificationState.value = Resource.Error(ex.message ?: "Some Error Occurred")
        }
    }

    fun addSwipedLeftForUser(userId: String) {
        userDatabase.child(userId).child(DATA_SWIPE_LEFT)
            .child("${firebaseAuth.currentUser?.uid}").setValue(true)
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun addSwipedRightForUser(selectedUserId: String, currentUser: User, selectedUser: User) {
        val userId = firebaseAuth.currentUser?.uid
        userId?.let {
            with(userDatabase) {
                child(userId).child(DATA_SWIPE_RIGHT)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.hasChild(selectedUserId)) {
                                currentUser.token?.let { tokenOne ->
                                    selectedUser.token?.let { tokenTwo ->
                                        sendNotificationToDevice(
                                            NotificationRequest(
                                                registrationIds = arrayListOf(tokenOne, tokenTwo),
                                                Notification(
                                                    title = "Looks like you found yourself a match ${
                                                        getEmojiByUnicode(0x1F49B)
                                                    }",
                                                    body = "See who you've matched with"
                                                )
                                            )
                                        )
                                    }
                                }
                                //Send Matched Notification to both device
                                val chatKey = chatDatabase.push().key
//
                                if (chatKey != null) {
                                    child(userId).child(DATA_SWIPE_RIGHT)
                                        .child(selectedUserId)
                                        .removeValue()
                                    child(userId).child(DATA_MATCHES)
                                        .child(selectedUserId)
                                        .setValue(chatKey)
                                    child(selectedUserId).child(DATA_MATCHES)
                                        .child(userId)
                                        .setValue(chatKey)

                                    chatDatabase.child(chatKey).child(userId)
                                        .child(DATA_NAME)
                                        .setValue(currentUser.name)
                                    chatDatabase.child(chatKey).child(userId)
                                        .child(
                                            DATA_PROFILE_PICTURE
                                        ).setValue(currentUser.profilePhotoUrl)
                                    chatDatabase.child(chatKey)
                                        .child(selectedUserId)
                                        .child(DATA_NAME)
                                        .setValue(selectedUser.name)
                                    chatDatabase.child(chatKey)
                                        .child(selectedUserId).child(
                                            DATA_PROFILE_PICTURE
                                        )
                                        .setValue(selectedUser.profilePhotoUrl)
                                }
                            } else {
                                child(selectedUserId).child(DATA_SWIPE_RIGHT)
                                    .child(userId)
                                    .setValue(true)
                                    .addOnFailureListener {
                                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }
}