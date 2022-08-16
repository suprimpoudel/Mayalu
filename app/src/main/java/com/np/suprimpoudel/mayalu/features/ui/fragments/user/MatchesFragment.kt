package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentMatchesBinding
import com.np.suprimpoudel.mayalu.features.listeners.UserCardClickListener
import com.np.suprimpoudel.mayalu.features.shared.adapter.MatchesAdapter
import com.np.suprimpoudel.mayalu.features.shared.model.User
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.user.MatchesViewModel
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hideBottomNavView
import com.np.suprimpoudel.mayalu.utils.extensions.showBottomNavView
import com.np.suprimpoudel.mayalu.utils.util.DialogUtil
import com.np.suprimpoudel.mayalu.utils.util.navigateWithDirections
import com.np.suprimpoudel.mayalu.utils.util.popBack
import com.np.suprimpoudel.mayalu.utils.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchesFragment : Fragment(), UserCardClickListener {
    private lateinit var binding: FragmentMatchesBinding
    private lateinit var matchesAdapter: MatchesAdapter
    private val matchedUser = mutableListOf<User>()

    private val viewModel: MatchesViewModel by viewModels()

    companion object {
        const val TAG = "DEBUG_DATA"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMatchesBinding.bind(view)

        activity?.hideBottomNavView()
        initRecyclerView()
        viewModel.fetchMatchedUserList()

        lifecycleScope.launchWhenStarted {
            viewModel.matchesList.collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        DialogUtil.hideDialog()
                        Log.d(TAG, "onViewCreated: Here")
                        val errorMessage = resource.message
                        errorMessage?.let {
                            showToast(it)
                        }
                        popBack()
                    }
                    is Resource.Success -> {
                        DialogUtil.hideDialog()
                        val list = resource.data
                        if (list?.isNotEmpty() == true) {
                            binding.lltNoMatches.isVisible = false
                            matchedUser.clear()
                            matchedUser.addAll(list)
                            matchesAdapter.notifyDataSetChanged()
                        } else {
                            binding.lltNoMatches.isVisible = true
                        }
                    }
                    is Resource.Loading -> DialogUtil.showDialog(requireContext())
                    else -> Unit
                }
            }
        }
    }

    override fun onUserCardClickListener(user: User) {
        val direction = MatchesFragmentDirections.actionMatchesFragmentToUserDetailFragment(
            username = user.name ?: "",
            profileImageUrl = user.profilePhotoUrl ?: "",
            gender = user.gender ?: "",
            age = user.age ?: 0,
            about = user.about ?: ""
        )
        navigateWithDirections(direction)
    }

    private fun initRecyclerView() {
        matchesAdapter = MatchesAdapter(matchedUser, this)
        binding.rcvMatches.apply {
            adapter = matchesAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    private fun fetchData() {
//        initDialog()
//        showDialog()
    }

    override fun onDestroyView() {
        activity?.showBottomNavView()
        super.onDestroyView()
    }
}