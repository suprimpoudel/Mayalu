package com.np.suprimpoudel.mayalu.features.ui.fragments.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentSignUpUploadPhotoBinding
import com.np.suprimpoudel.mayalu.features.shared.model.User
import com.np.suprimpoudel.mayalu.features.ui.activity.UserActivity
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant.DATA_PROFILE_PICTURE
import com.np.suprimpoudel.mayalu.utils.extensions.hasInternetConnection
import com.np.suprimpoudel.mayalu.utils.util.DialogUtil
import com.np.suprimpoudel.mayalu.utils.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SignUpUploadPhotoFragment(
) : Fragment() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("firebaseDatabase")
    lateinit var firebaseDatabase: FirebaseDatabase

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private lateinit var binding: FragmentSignUpUploadPhotoBinding
    private var profilePhotoURI: Uri? = null

    private val args by navArgs<SignUpUploadPhotoFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_sign_up_upload_photo,
            container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }


    private fun initListener() {
        binding.mcvInputImage.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnContinue.setOnClickListener {
            if (profilePhotoURI != null) {
                if (context.hasInternetConnection()) {
                    DialogUtil.showDialog(requireContext())
                    uploadImageToBucket()
                } else {
                    showToast("No Internet Connection")
                    return@setOnClickListener
                }
            } else {
                showToast("Please select a image first")
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val uri = data.data
                    binding.imvInputImage.setImageURI(uri)
                    binding.imvInputImage.scaleType = ImageView.ScaleType.FIT_XY
                    profilePhotoURI = uri
                }
            }
        }

    private fun uploadImageToBucket() {
        val storageReference =
            firebaseStorage.getReference(DATA_PROFILE_PICTURE).child(firebaseAuth.uid ?: "")
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                context?.applicationContext?.contentResolver,
                profilePhotoURI
            )
        } catch (e: Exception) {
            DialogUtil.hideDialog()
            showToast(e.message)
        }
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 20, baos)
        val bitmapData = baos.toByteArray()
        storageReference.putBytes(bitmapData)
            .addOnFailureListener {
                DialogUtil.hideDialog()
                showToast(it.message)
            }
            .addOnSuccessListener { _ ->
                storageReference.downloadUrl
                    .addOnSuccessListener { url ->
                        uploadDataToFirebase(url)
                    }
            }

    }

    private fun uploadDataToFirebase(profileImageUrl: Uri) {
        val user = User(
            uid = firebaseAuth.uid,
            name = args.name,
            gender = args.gender,
            verified = true,
            age = args.age,
            lookingFor = args.lookingFor,
            about = "",
            token = "",
            profilePhotoUrl = profileImageUrl.toString()
        )

        firebaseDatabase.getReference(FirebaseConstant.DATA_USERS)
            .child(firebaseAuth.uid ?: "")
            .setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    DialogUtil.hideDialog()
                    startActivity(Intent(context, UserActivity::class.java))
                    activity?.finish()
                } else {
                    DialogUtil.hideDialog()
                    showToast(task.exception?.message ?: "Some Error Occurred")
                }
            }
            .addOnFailureListener {
                DialogUtil.hideDialog()
                showToast(it.message)
            }
    }
}