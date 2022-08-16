package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentEditProfileBinding
import com.np.suprimpoudel.mayalu.features.shared.model.User
import com.np.suprimpoudel.mayalu.features.shared.widgets.CustomLoadingDialog
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.shared.UserViewModel
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.user.EditProfileViewModel
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.extensions.hideBottomNavView
import com.np.suprimpoudel.mayalu.utils.extensions.showBottomNavView
import com.np.suprimpoudel.mayalu.utils.util.DialogUtil
import com.np.suprimpoudel.mayalu.utils.util.popBack
import com.np.suprimpoudel.mayalu.utils.util.showSnackbar
import com.np.suprimpoudel.mayalu.utils.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var userViewModel: UserViewModel
    private val viewModel: EditProfileViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var loadingDialog: CustomLoadingDialog

    private var pickedImageUri: Uri? = null
    private var isUploading = false

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
            R.layout.fragment_edit_profile,
            container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        activity?.hideBottomNavView()

        initListener()

        userViewModel.getUserInfo()
        getUserInfo()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.btnChangePhoto.id -> openGalleryToPickImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)
        menu.removeItem(R.id.changePassword)
        menu.removeItem(R.id.iconAdd)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.iconSave -> updateProfileDetails()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserInfo() {
        lifecycleScope.launchWhenStarted {
            userViewModel.userState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtil.showDialog(requireContext())
                    }
                    is Resource.Error -> {
                        val errorMessage = resource.message
                        errorMessage?.let {
                            DialogUtil.hideDialog()
                            showToast(it)
                            popBack()
                        }
                    }
                    is Resource.Success -> {
                        val user = resource.data
                        user?.let {
                            DialogUtil.hideDialog()
                            populateData(it)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initListener() {
        binding.btnChangePhoto.setOnClickListener(this)
    }

    private fun populateData(user: User) {
        binding.edtAboutMe.setText(user.about)
        binding.edtDisplayName.setText(user.name)
        binding.edtGender.setText(user.gender)
        glide.load(user.profilePhotoUrl).into(binding.imvProfilePicture)

        when (user.lookingFor) {
            "Male" -> binding.rbtnMaleIntrested.id.let {
                binding.rdgLookingFor.check(
                    it
                )
            }
            "Female" -> binding.rbtnFemaleIntrested.id.let {
                binding.rdgLookingFor.check(
                    it
                )
            }
            else -> binding.rbtnFBothIntrested.id.let {
                binding.rdgLookingFor.check(
                    it
                )
            }
        }
    }

    private fun updateProfileDetails() {
        val name = binding.edtDisplayName.text.toString().trim()
        val about = binding.edtAboutMe.text.toString().trim()
        val checkedId = binding.rdgLookingFor.checkedRadioButtonId
        val lookingFor = checkedId.let { activity?.findViewById<RadioButton>(it)?.text.toString() }

        if (name.isEmpty() || about.isEmpty()) {
            return
        } else {
            viewModel.updateProfileDetails(name, about, lookingFor)
        }

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
                        if (pickedImageUri != null && !isUploading) {
                            uploadImageToBucket(pickedImageUri!!)
                        } else {
                            showSnackbar("Successfully Updated Profile")
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun uploadImageToBucket(it: Uri) {
        userViewModel.uploadImageToFirebaseStorage(it)
        lifecycleScope.launchWhenStarted {
            userViewModel.bucketState.collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        isUploading = false
                        val errorMessage = resource.message
                        showToast(errorMessage)
                    }
                    is Resource.Success -> {
                        isUploading = false
                        showSnackbar("Successfully Updated Profile")
                    }
                    is Resource.Loading -> {
                        isUploading = true
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun openGalleryToPickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    pickedImageUri = it
                    binding.imvProfilePicture.setImageURI(it)
                }
            }
        }


    override fun onDestroyView() {
        activity?.showBottomNavView()
        super.onDestroyView()
    }
}