package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentChatListBinding
import com.np.suprimpoudel.mayalu.features.listeners.ChatTileClickListener
import com.np.suprimpoudel.mayalu.features.shared.adapter.ChatTileAdapter
import com.np.suprimpoudel.mayalu.features.shared.model.Chat
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.user.ChatListViewModel
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.util.navigateWithDirections
import com.np.suprimpoudel.mayalu.utils.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ChatListFragment : Fragment(), ChatTileClickListener {
    private lateinit var binding: FragmentChatListBinding
    private lateinit var chatTileAdapter: ChatTileAdapter
    private val viewModel: ChatListViewModel by viewModels()

    private val chatList = mutableListOf<Chat>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_chat_list,
            container, false
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        viewModel.getChatList()
        getData()
    }

    private fun initRecyclerView() {
        chatTileAdapter = ChatTileAdapter(chatList, this)
        binding.rcvChatList.apply {
            adapter = chatTileAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getData() {
        lifecycleScope.launchWhenStarted {
            viewModel.chatState.collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val list = resource.data
                        chatList.clear()
                        chatList.addAll(list ?: emptyList())
                        chatTileAdapter.notifyDataSetChanged()
                    }
                    is Resource.Error -> {
                        showToast(resource.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onChatTileClicked(chat: Chat) {
        val direction = ChatListFragmentDirections.actionChatListFragmentToChatFragment(
            chatId = chat.chatId ?: "",
            nickname = chat.nickname ?: "",
            profileImageUrl = chat.profileUrl ?: ""
        )
        navigateWithDirections(direction)
    }
}