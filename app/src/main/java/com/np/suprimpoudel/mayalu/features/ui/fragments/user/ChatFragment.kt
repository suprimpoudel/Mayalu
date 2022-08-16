package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentChatBinding
import com.np.suprimpoudel.mayalu.features.shared.adapter.MessageAdapter
import com.np.suprimpoudel.mayalu.features.shared.model.ChatData
import com.np.suprimpoudel.mayalu.features.ui.fragments.bottomsheet.GalleryBottomDialogFragment
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.user.ChatViewModel
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_MESSAGES
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hideBottomNavView
import com.np.suprimpoudel.mayalu.utils.extensions.showBottomNavView
import com.np.suprimpoudel.mayalu.utils.util.popBack
import com.np.suprimpoudel.mayalu.utils.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ChatFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<ChatData>()

    private val args by navArgs<ChatFragmentArgs>()
    private val viewModel: ChatViewModel by viewModels()

    private var fileName: String? = null
    private var bucketUrl: String? = null

    private val chatListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val chat = snapshot.getValue(ChatData::class.java)
            if (chat != null) {
                messageList.add(chat)
                messageAdapter.notifyItemInserted(messageList.size - 1)
                binding.rcvChats.smoothScrollToPosition(messageList.size - 1)

                binding.constraintPictureData.isVisible = false
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_chat,
            container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.hideBottomNavView()

        setData()
        initListener()
        initRecyclerView()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.imvGallery.id -> openGalleryBottomSheet()
            binding.txvSend.id -> sendMessage()
            binding.imvClose.id -> deleteFromStorage()
        }
    }

    override fun onDestroyView() {
        activity?.showBottomNavView()
        super.onDestroyView()
    }

    private fun setData() {
        val username = args.nickname
        val profileImageUrl = args.profileImageUrl

        binding.txvUsername.text = username
        with(binding.chatToolbar) {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                popBack()
            }
        }

        Glide.with(binding.imvProfilePicture.context)
            .load(profileImageUrl)
            .error(R.drawable.mayalu_app_icon)
            .placeholder(R.drawable.mayalu_app_icon)
            .into(binding.imvProfilePicture)
    }

    private fun initListener() {
        binding.imvGallery.setOnClickListener(this)
        binding.txvSend.setOnClickListener(this)
        binding.imvClose.setOnClickListener(this)
    }

    private fun initRecyclerView() {
        messageAdapter = MessageAdapter(messageList, viewModel.firebaseAuth.currentUser?.uid ?: "")
        with(binding.rcvChats) {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.chatDatabase.child(args.chatId).child(DATA_MESSAGES)
            .addChildEventListener(chatListener)
    }

    private fun sendMessage() {
        val simpleDate = SimpleDateFormat("hh:mm")
        val time = simpleDate.format(Date())
        val message = binding.edtMessage.text.toString().trim()
        if (message.isNotEmpty() || bucketUrl != null) {
            val chat = ChatData(
                sentBy = viewModel.firebaseAuth.currentUser?.uid,
                message = message,
                timeStamp = time,
                pictureUrl = bucketUrl ?: ""
            )
            bucketUrl = null
            val key = viewModel.chatDatabase.child(args.chatId).child(DATA_MESSAGES).push().key
            if (!key.isNullOrEmpty()) {
                viewModel.chatDatabase.child(args.chatId).child(DATA_MESSAGES).child(key)
                    .setValue(chat)
                    .addOnSuccessListener {
                        binding.edtMessage.setText("")
                        Log.d("DEBUG_DATA", "${viewModel.firebaseAuth.currentUser?.uid}")
                    }
                    .addOnFailureListener {
                        showToast(it.message)
                    }
            }
        }
    }

    private fun openGalleryBottomSheet() {
        val galleryDialog = GalleryBottomDialogFragment() { photo ->
            photo.contentUri?.let {
                fileName = photo.name
                val byteData = convertUriToByteArray(it)
                byteData?.let {
                    viewModel.uploadToBucket(byteData, photo.name)
                    listenToBucket()
                }
            }
        }
        galleryDialog.show(childFragmentManager, "Gallery Dialog")
    }

    private fun convertUriToByteArray(uri: Uri): ByteArray? {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(
                context?.applicationContext?.contentResolver,
                uri
            )
            val byteOutputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 80, byteOutputStream)
            byteOutputStream.toByteArray()
        } catch (e: Exception) {
            showToast(e.message)
            null
        }
    }

    private fun listenToBucket() {
        viewModel.photoUploadState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> Unit
                is Resource.Error -> {
                    val errorMessage = resource.message
                    showToast(errorMessage)
                }
                is Resource.Success -> {
                    binding.constraintPictureData.isVisible = true
                    binding.txvFileName.text = fileName

                    bucketUrl = resource.data?.pictureUrl
                }
                else -> Unit
            }
        }
    }

    private fun deleteFromStorage() {
        fileName?.let {
            viewModel.deleteFromBucket(it)
            fileName = null
            binding.constraintPictureData.isVisible = false
        }
    }
}