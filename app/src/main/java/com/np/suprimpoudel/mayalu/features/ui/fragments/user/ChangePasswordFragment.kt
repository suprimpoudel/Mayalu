package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentChangePasswordBinding
import com.np.suprimpoudel.mayalu.features.shared.base.BaseFragment
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.shared.UserViewModel
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hideBottomNavView
import com.np.suprimpoudel.mayalu.utils.extensions.hideKeyboard
import com.np.suprimpoudel.mayalu.utils.extensions.showBottomNavView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_change_password,
            container, false
        )
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)
        menu.removeItem(R.id.iconSave)
        menu.removeItem(R.id.iconAdd)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changePassword -> {
                updatePassword()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.hideBottomNavView()
    }

    private fun updatePassword() {
        view?.hideKeyboard()
        val currentPassword = binding.edtCurrentPassword.text.toString().trim()
        val newPassword = binding.edtNewPassword.text.toString().trim()
        val retypePassword = binding.edtRetypePassword.text.toString().trim()

        if (isValidInput(currentPassword, newPassword, retypePassword)) {
            updatePasswordInFirebase(currentPassword, newPassword)
        } else {
            return
        }
    }

    private fun updatePasswordInFirebase(currentPassword: String, newPassword: String) {
        viewModel.updatePassword(currentPassword, newPassword)
        lifecycleScope.launchWhenStarted {
            viewModel.userState.collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        val errorMessage = resource.message
                        errorMessage?.let {
                            showToast(it)
                        }
                    }
                    is Resource.Success -> {
                        showSnackbar("Successfully Updated Password")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun isValidInput(
        currentPassword: String?,
        newPassword: String?,
        retypePassword: String?,
    ): Boolean {
        if (currentPassword?.isEmpty() == true) {
            setError(binding.tilCurrentPassword, getString(R.string.field_cannot_be_empty))
            return false
        } else removeError(binding.tilCurrentPassword)
        if (newPassword?.isEmpty() == true) {
            setError(binding.tilNewPassword, getString(R.string.field_cannot_be_empty))
            return false
        } else removeError(binding.tilNewPassword)
        if (retypePassword?.isEmpty() == true) {
            setError(binding.tilRetypePassword, getString(R.string.field_cannot_be_empty))
            return false
        } else removeError(binding.tilRetypePassword)
        if (newPassword != retypePassword) {
            setError(binding.tilRetypePassword, "New password and retype password doesn't match")
            return false
        } else removeError(binding.tilRetypePassword)
        return true
    }

    override fun onDestroyView() {
        activity?.showBottomNavView()
        super.onDestroyView()
    }
}