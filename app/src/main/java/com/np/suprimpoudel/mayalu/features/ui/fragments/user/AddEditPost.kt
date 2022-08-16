package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentAddEditPostBinding
import com.np.suprimpoudel.mayalu.features.shared.model.Post
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.user.PostViewModel
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hideBottomNavView
import com.np.suprimpoudel.mayalu.utils.util.DialogUtil
import com.np.suprimpoudel.mayalu.utils.util.popBack
import com.np.suprimpoudel.mayalu.utils.util.showSnackbar
import com.np.suprimpoudel.mayalu.utils.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditPost : Fragment(), View.OnClickListener {
    val TAG = "DEBUG_DATA"
    private lateinit var binding: FragmentAddEditPostBinding

    private val args by navArgs<AddEditPostArgs>()

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_edit_post, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.hideBottomNavView()
        initListener()
        if (args.post != null) {
            populateData(args.post!!)
        }
    }

    private fun populateData(post: Post) {
        binding.edtThoughts.setText(post.postContent)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.btnSave.id -> saveUpdate()
        }
    }

    private fun initListener() {
        binding.btnSave.setOnClickListener(this)
    }

    private fun saveUpdate() {
        savePost()
    }

    private fun savePost() {
        val content = binding.edtThoughts.text.toString().trim()
        if (content.isNotEmpty()) {
            val simpleDate = SimpleDateFormat("yyyy-MM-dd")
            val date = simpleDate.format(Date())
            val post = Post(
                postId = args.post?.postId,
                postDate = date,
                postBy = args.userName,
                postUserId = viewModel.userId,
                postUserPhotoUrl = args.profilePicture,
                postContent = content,
            )
            viewModel.savePost(post)
            viewModel.postStatus.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Error -> {
                        DialogUtil.hideDialog()
                        val error = resource.message
                        showToast(error)
                    }
                    is Resource.Success -> {
                        DialogUtil.hideDialog()
                        showSnackbar("Updated post")
                        popBack()
                    }
                    is Resource.Loading -> DialogUtil.showDialog(requireContext())
                    else -> Unit
                }
            }
        }
    }
}