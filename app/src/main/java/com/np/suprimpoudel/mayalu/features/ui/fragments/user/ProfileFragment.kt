package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentProfileBinding
import com.np.suprimpoudel.mayalu.features.shared.base.BaseFragment
import com.np.suprimpoudel.mayalu.features.shared.model.User
import com.np.suprimpoudel.mayalu.features.ui.activity.AuthActivity
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.shared.UserViewModel
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.showBottomNavView
import com.np.suprimpoudel.mayalu.utils.util.navigateWithDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment(
) : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var userViewModel: UserViewModel

    @Inject
    lateinit var glide: RequestManager

    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_profile,
            container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        activity?.showBottomNavView()
        getUserInfo()
        initListener()
    }

    private fun getUserInfo() {
        userViewModel.getUserInfo()
        lifecycleScope.launchWhenStarted {
            userViewModel.userState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
//                        showDialog()
                    }
                    is Resource.Error -> {
//                        dismissDialog()
                        val errorMessage = resource.message
                        errorMessage?.let {
                            showToast(it)
                        }
                    }
                    is Resource.Success -> {
//                        dismissDialog()
                        val user = resource.data
                        user?.let {
                            populateData(it)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun populateData(user: User) {
        currentUser = user
        glide.load(user.profilePhotoUrl).into(binding.imvProfilePicture)
        binding.txvDisplayName.text = user.name
        binding.txvAgeGender.text = "${user.gender}, ${user.age}"
    }

    private fun initListener() {
        binding.mcvEditProfile.setOnClickListener {
            navigateTo(R.id.action_profileFragment_to_editProfileFragment)
        }
        binding.mcvLogout.setOnClickListener {
            signOut()
        }
        binding.mcvContactUs.setOnClickListener {
            navigateTo(R.id.action_profileFragment_to_contactUsFragment)
        }
        binding.mcvChangePassword.setOnClickListener {
            navigateTo(R.id.action_profileFragment_to_changePasswordFragment)
        }
        binding.mcvMatches.setOnClickListener {
            navigateTo(R.id.action_profileFragment_to_matchesFragment)
        }
        binding.mcvPosts.setOnClickListener {
            if (currentUser != null) {
                val direction = ProfileFragmentDirections.actionProfileFragmentToMyPostsFragment(
                    userName = currentUser?.name ?: "",
                    profilePicture = currentUser?.profilePhotoUrl ?: ""
                )
                navigateWithDirections(direction)
            } else {
                showToast("Some error occurred")
            }
        }
    }

    private fun signOut() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("YES") { dialog, _ ->
                userViewModel.logout()
                dialog.dismiss()
                val intent = Intent(context, AuthActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}