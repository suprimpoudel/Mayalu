package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentHotBinding
import com.np.suprimpoudel.mayalu.features.shared.adapter.PostAdapter
import com.np.suprimpoudel.mayalu.features.shared.model.Post
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.user.PostViewModel
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_MATCHES
import com.np.suprimpoudel.mayalu.utils.extensions.hasInternetConnection
import com.np.suprimpoudel.mayalu.utils.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HotFragment : Fragment() {
    private lateinit var binding: FragmentHotBinding
    private lateinit var postAdapter: PostAdapter

    private val viewModel: PostViewModel by viewModels()

    private val postList: MutableList<Post> = mutableListOf()
    private val matchedUsersId = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hot, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        initRecyclerView()
        if(context.hasInternetConnection()) {
            getCurrentUser()
        } else {
            showToast("No Internet Connection")
        }
    }

    private fun initRecyclerView() {
        postAdapter = PostAdapter(postList,
            onDeleteIconClicked = { _, _ ->
            },
            onEditIconClicked = {
            }, currentUserId = viewModel.userId)
        binding.rcvMyPosts.adapter = postAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllData() {
        postList.clear()
        postAdapter.notifyDataSetChanged()
        viewModel.postDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    snapshot.children.forEach {
                        val key = it.key ?: ""
                        viewModel.postDatabase.child(key)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val post = snapshot.getValue(Post::class.java)
                                    if (post != null) {
                                        if (post.postUserId != viewModel.userId && matchedUsersId.contains(
                                                post.postUserId)
                                        ) {
                                            Log.d("DEBUG_DATA", "onDataChange: $post")
                                            postList.add(post)
                                            postAdapter.notifyItemInserted(postList.size - 1)
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    showToast(error.message)
                                }
                            })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast(error.message)
            }
        })
    }

    private fun getCurrentUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userDatabase.child(viewModel.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(DATA_MATCHES)) {
                            viewModel.userDatabase.child(viewModel.userId).child(DATA_MATCHES)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        snapshot.children.forEach {
                                            matchedUsersId.add(it.key ?: "")
                                        }
                                        getAllData()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        showToast(error.message)
                                    }
                                })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showToast(error.message)
                    }
                })
        }
    }
}