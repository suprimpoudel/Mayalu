package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentSwipeBinding
import com.np.suprimpoudel.mayalu.features.listeners.SwipeCardOnClickListener
import com.np.suprimpoudel.mayalu.features.shared.adapter.SwipeCardAdapter
import com.np.suprimpoudel.mayalu.features.shared.base.BaseFragment
import com.np.suprimpoudel.mayalu.features.shared.model.User
import com.np.suprimpoudel.mayalu.features.shared.model.request.Notification
import com.np.suprimpoudel.mayalu.features.shared.model.request.NotificationRequest
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.shared.UserViewModel
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.user.SwipeViewModel
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_MATCHES
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_SWIPE_LEFT
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_SWIPE_RIGHT
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hasInternetConnection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SwipeFragment : BaseFragment(), SwipeCardOnClickListener {

    companion object {
        const val TAG = "DEBUG_DATA"
    }

    private lateinit var binding: FragmentSwipeBinding
    private val userViewModel: UserViewModel by viewModels()
    private val viewModel: SwipeViewModel by viewModels()

    private val listOfUsers = mutableListOf<User>()
    private lateinit var swipeCardAdapter: ArrayAdapter<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_swipe,
            container, false
        )
        return binding.root
    }

    private var currentUser: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context.hasInternetConnection()) {
            showSearching()
            initAdapter()
            populateItems()
            getCurrentUser()

            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        userViewModel.registerDeviceInRealtimeDatabase(token)
                    } else {
                        Log.d(TAG, "onCreate: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "onCreate: ${it.message}")
                }
        } else {
            showToast("No Internet Connection")
        }
    }

    private fun initAdapter() {
        swipeCardAdapter =
            context?.let { SwipeCardAdapter(it, R.layout.item_swipe, listOfUsers, this) }!!
        binding.swipeFrames.adapter = swipeCardAdapter
        binding.swipeFrames.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                listOfUsers.removeAt(0)
                swipeCardAdapter.notifyDataSetChanged()
                if (listOfUsers.isEmpty()) {
                    showEmptyLayout()
                } else {
                    showPopulatedLayout()
                }
            }

            override fun onLeftCardExit(u: Any?) {
                val user = u as User
                user.uid?.let {
                    viewModel.addSwipedLeftForUser(it)
                }
            }

            override fun onRightCardExit(u: Any?) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val user = u as User
                    user.token?.let {
                        sendNotification(it)
                        user.uid?.let { userId ->
                            currentUser?.let { cUser ->
                                viewModel.addSwipedRightForUser(
                                    userId,
                                    cUser,
                                    user
                                )
                            }
                        }
                    }
                }
            }

            override fun onAdapterAboutToEmpty(p0: Int) {
            }

            override fun onScroll(p0: Float) {
            }
        })
    }

    private fun getCurrentUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = viewModel.firebaseAuth.currentUser?.uid
            userId?.let {
                try {
                    if (context.hasInternetConnection()) {
                        viewModel.userDatabase.child(userId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val user = snapshot.getValue(User::class.java)
                                    user?.let {
                                        currentUser = it
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.d(TAG, "onCancelled: ${error.message}")
                                }
                            })
                    } else {
//                         Show("No Internet Connection")
                    }
                } catch (ex: Exception) {
                    showToast(message = ex.message ?: "Some Error Occurred")
                }
            }
        }
    }

    private fun populateItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            listOfUsers.clear()
            swipeCardAdapter.notifyDataSetChanged()
            showSearching()
            hideEverythingExceptSearching()
            val userId = viewModel.firebaseAuth.currentUser?.uid
            userId?.let { uid ->
                viewModel.userDatabase.orderByChild("gender")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(snapshot: DatabaseError) {
                            showToast(snapshot.message)
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            hideSearching()
                            if (snapshot.hasChildren()) {
                                snapshot.children.forEach { child ->
                                    val user = child.getValue(User::class.java)
                                    if (user != null) {
                                        var showUser = true
                                        if (child.child(DATA_SWIPE_LEFT).hasChild(userId) ||
                                            child.child(DATA_SWIPE_RIGHT).hasChild(userId) ||
                                            child.child(DATA_MATCHES).hasChild(userId)
                                        ) {
                                            showUser = false
                                        }
                                        if (user.uid != uid && showUser && user.uid?.isNotEmpty() == true) {
                                            if (currentUser?.lookingFor == "Both") {
                                                listOfUsers.add(user)
                                                swipeCardAdapter.notifyDataSetChanged()
                                            } else {
                                                if (user.gender == currentUser?.lookingFor) {
                                                    listOfUsers.add(user)
                                                    swipeCardAdapter.notifyDataSetChanged()
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                            if (listOfUsers.isNotEmpty()) {
                                binding.swipeFrames.visibility = View.VISIBLE
                            } else {
                                binding.lltNoMoreUsers.visibility = View.VISIBLE
                            }
                        }
                    })
            }
        }
    }

    override fun onLikeButtonClicked(user: User) {
        binding.swipeFrames.topCardListener?.selectLeft()
        user.token?.let {
            sendNotification(it)
        }
    }

    override fun onDislikeButtonClicked() {
        binding.swipeFrames.topCardListener?.selectRight()
    }

    override fun onSwipeCardClicked(user: User) {
        val action = SwipeFragmentDirections.actionSwipeFragmentToUserDetailFragment(
            username = user.name ?: "",
            profileImageUrl = user.profilePhotoUrl ?: "",
            gender = user.gender ?: "",
            age = user.age ?: 0,
            about = user.about ?: ""
        )
        findNavController().navigate(action)
    }

    private fun showSearching() {
        binding.lltSearching.visibility = View.VISIBLE
    }

    private fun hideSearching() {
        binding.lltSearching.visibility = View.GONE
    }

    private fun hideEverythingExceptSearching() {
        binding.swipeFrames.visibility = View.GONE
        binding.lltNoMoreUsers.visibility = View.GONE
    }

    private fun showEmptyLayout() {
        binding.swipeFrames.visibility = View.GONE
        binding.lltNoMoreUsers.visibility = View.VISIBLE
    }

    private fun showPopulatedLayout() {
        binding.swipeFrames.visibility = View.VISIBLE
        binding.lltNoMoreUsers.visibility = View.GONE
    }

    private fun sendNotification(token: String) {
        val fireUniCode = 0x1F525
        val notificationRequest = NotificationRequest(
            registrationIds = arrayListOf(token),
            notification = Notification(
                title = "You are trending right now ${getEmojiByUnicode(fireUniCode)}",
                body = "Someone just liked your profile"
            )
        )
        viewModel.sendNotificationToDevice(notificationRequest)
        viewModel.notificationState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    val errorMessage = resource.message
                    errorMessage?.let {
                        Log.d(TAG, "sendNotification: $it")
                    }
                }
                else -> Unit
            }
        }
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }
}