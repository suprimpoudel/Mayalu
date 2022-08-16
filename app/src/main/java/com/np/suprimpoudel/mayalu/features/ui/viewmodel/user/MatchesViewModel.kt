package com.np.suprimpoudel.mayalu.features.ui.viewmodel.user

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.np.suprimpoudel.mayalu.features.application.MyApplication
import com.np.suprimpoudel.mayalu.features.shared.model.User
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.base.BaseViewModel
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_MATCHES
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @Named("userDatabase")
    private val userDatabase: DatabaseReference,
    application: MyApplication,
) : BaseViewModel(application) {
    private val _matchesList = MutableStateFlow<Resource<MutableList<User>>>(Resource.Empty())
    val matchesList = _matchesList

    private val matchesListUser = mutableListOf<User>()

    companion object {
        const val TAG = "DEBUG_DATA"
    }

    fun fetchMatchedUserList() =
        viewModelScope.launch {
            _matchesList.value = Resource.Loading()
            val userId = firebaseAuth.currentUser?.uid
            userId?.let {
                if (context.hasInternetConnection()) {
                    userDatabase.child(it).child(DATA_MATCHES)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.hasChildren()) {
                                    snapshot.children.forEach { child ->
                                        val matchId = child.key
                                        if (matchId?.isEmpty() == false) {
                                            userDatabase.child(matchId)
                                                .addListenerForSingleValueEvent(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        val user = snapshot.getValue(User::class.java)
                                                        user?.let { u ->
                                                            matchesListUser.add(u)
                                                            _matchesList.value =
                                                                Resource.Success(data = matchesListUser)
                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        _matchesList.value =
                                                            Resource.Error(error.message)
                                                    }
                                                })
                                        } else {
                                            _matchesList.value =
                                                Resource.Success(data = matchesListUser)
                                        }
                                    }
                                } else {
                                    _matchesList.value = Resource.Success(matchesListUser)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                _matchesList.value = Resource.Error(error.message)
                            }
                        })
                }
            }
        }
}