package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentMyPostsBinding
import com.np.suprimpoudel.mayalu.features.shared.adapter.PostAdapter
import com.np.suprimpoudel.mayalu.features.shared.model.Post
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.user.PostViewModel
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hideBottomNavView
import com.np.suprimpoudel.mayalu.utils.extensions.showBottomNavView
import com.np.suprimpoudel.mayalu.utils.util.navigateWithDirections
import com.np.suprimpoudel.mayalu.utils.util.popBack
import com.np.suprimpoudel.mayalu.utils.util.showSnackbar
import com.np.suprimpoudel.mayalu.utils.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPostsFragment : Fragment() {
    private lateinit var binding: FragmentMyPostsBinding
    private lateinit var postAdapter: PostAdapter

    private val viewModel: PostViewModel by viewModels()

    private val postList: MutableList<Post> = mutableListOf()

    private val args by navArgs<MyPostsFragmentArgs>()

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_posts, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.hideBottomNavView()
        initRecyclerView()
        getAllPosts()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)
        menu.removeItem(R.id.changePassword)
        menu.removeItem(R.id.iconSave)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.iconAdd -> navigateToAddScreen()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToAddScreen() {
        val directions = MyPostsFragmentDirections.actionMyPostsFragmentToAddEditPost(null,
            userName = args.userName,
            profilePicture = args.profilePicture)
        navigateWithDirections(directions)
    }

    private fun initRecyclerView() {
        postAdapter = PostAdapter(postList,
            onDeleteIconClicked = { post, position ->
                showDialogToDelete(post, position)
            },
            onEditIconClicked = {
                val directions = MyPostsFragmentDirections.actionMyPostsFragmentToAddEditPost(
                    post = it,
                    profilePicture = args.profilePicture,
                    userName = args.userName
                )
                navigateWithDirections(directions)
            }, currentUserId = viewModel.userId)
        with(binding.rcvMyPosts) {
            adapter = postAdapter
        }
    }

    private fun showDialogToDelete(post: Post, position: Int) {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                deletePost(post, position)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun deletePost(post: Post, position: Int) {
        viewModel.deletePost(post)
        viewModel.postStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    val error = resource.message
                    showToast(error)
                }
                is Resource.Success -> {
                    showSnackbar("Deleted Post")
                    postList.remove(post)
                    postAdapter.notifyItemRemoved(position)
                }
                else -> Unit
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllPosts() {
        postList.clear()
        postAdapter.notifyDataSetChanged()
        viewModel.postDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    snapshot.children.forEach {
                        val postKey = it.key
                        if (postKey?.isNotEmpty() == true) {
                            viewModel.postDatabase.child(postKey)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val post = snapshot.getValue(Post::class.java)
                                        if (post != null) {
                                            if (post.postUserId == viewModel.userId) {
                                                val newPost = post.copy(
                                                    postBy = args.userName,
                                                    postUserPhotoUrl = args.profilePicture
                                                )
                                                postList.add(newPost)
                                                postAdapter.notifyItemInserted(postList.size - 1)
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        showToast(error.message)
                                        popBack()
                                    }
                                })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast(error.message)
                popBack()
            }
        })
    }
}