package com.np.suprimpoudel.mayalu.features.ui.viewmodel.user

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.np.suprimpoudel.mayalu.features.application.MyApplication
import com.np.suprimpoudel.mayalu.features.shared.model.Chat
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.base.BaseViewModel
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_MESSAGES
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_NAME
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_PROFILE_PICTURE
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ChatListViewModel @Inject constructor(
    application: MyApplication,
    val firebaseAuth: FirebaseAuth,
    @Named("userDatabase")
    val userDatabase: DatabaseReference,
    @Named("chatDatabase")
    val chatDatabase: DatabaseReference,
) : BaseViewModel(application) {
    private val _chatState = MutableStateFlow<Resource<List<Chat>>>(Resource.Empty())
    val chatState = _chatState

    private val chatList = mutableListOf<Chat>()

    fun getChatList() =
        viewModelScope.launch {
            if (context.hasInternetConnection()) {
                _chatState.value = Resource.Loading()
                chatList.clear()
                val userId = firebaseAuth.currentUser?.uid
                userId?.let { uId ->
                    chatDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                if (it.hasChild(uId)) {
                                    val chatKey = it.key
                                    chatKey?.let {
                                        chatDatabase.child(chatKey)
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    snapshot.children.forEach { snapshot ->
                                                        if (snapshot.key != uId && snapshot.key != DATA_MESSAGES) {
                                                            val name =
                                                                snapshot.child(DATA_NAME).value as String
                                                            val profileUrl =
                                                                snapshot.child(DATA_PROFILE_PICTURE).value as String
                                                            val chat = Chat(
                                                                chatId = chatKey,
                                                                nickname = name,
                                                                profileUrl = profileUrl
                                                            )
                                                            chatList.add(chat)
                                                            _chatState.value =
                                                                Resource.Success(data = chatList)
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                }
                                            })
                                    }
                                } else {
                                    _chatState.value = Resource.Success(data = chatList)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }
            } else {
                _chatState.value = Resource.Error("No Internet Connection")
            }
        }
}