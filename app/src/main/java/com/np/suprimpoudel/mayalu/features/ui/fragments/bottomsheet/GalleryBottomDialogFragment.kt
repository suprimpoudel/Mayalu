package com.np.suprimpoudel.mayalu.features.ui.fragments.bottomsheet

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.np.suprimpoudel.mayalu.databinding.FragmentGalleryBottomDialogBinding
import com.np.suprimpoudel.mayalu.features.shared.adapter.GalleryAdapter
import com.np.suprimpoudel.mayalu.features.shared.model.Photo
import com.np.suprimpoudel.mayalu.features.ui.viewmodel.user.GalleryViewModel
import com.np.suprimpoudel.mayalu.utils.coroutines.Resource
import com.np.suprimpoudel.mayalu.utils.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class GalleryBottomDialogFragment(
    private val getData: (Photo) -> Unit,
) : BottomSheetDialogFragment() {
    companion object {
        val TAG = GalleryBottomDialogFragment::class.java.name
    }

    private lateinit var binding: FragmentGalleryBottomDialogBinding
    private lateinit var galleryAdapter: GalleryAdapter

    private val viewModel: GalleryViewModel by viewModels()
    private val photoList = mutableListOf<Photo>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 12)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGalleryBottomDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        requestPermissionLauncher.launch(
            Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                fetchPictures()
            } else {
                onUserAction()
            }
        }

    private fun initRecyclerView() {
        galleryAdapter = GalleryAdapter(photoList) { photo ->
            getData(photo)
            dialog?.dismiss()
        }
        binding.rcvGallery.apply {
            adapter = galleryAdapter
            layoutManager = GridLayoutManager(context, 3)
        }
    }

    private fun onUserAction() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                fetchPictures()
            }
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED -> {
                showToast("Storage permission is required to display pictures from your device")
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(requireContext())
                    .setMessage("Storage permission is required to display pictures from your device")
                    .setPositiveButton("Grant") { dialog, _ ->
                        dialog?.dismiss()
                        requestPermissionLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    .setNegativeButton("Deny") { dialog, _ ->
                        dialog?.dismiss()
                    }
                    .setOnDismissListener {
                        dialog?.dismiss()
                    }.show()
                Log.d(TAG, "onUserAction: Show Alert Dialog")
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun fetchPictures() {
        viewModel.getAllPictures(requireContext().contentResolver)
        viewModel.listOfPhotos.observe(viewLifecycleOwner) {
            Log.d(TAG, "onViewCreated: ${it.size}")
            updateRecyclerView(it)
        }
    }

    private fun updateRecyclerView(photoList: List<Photo>) {
        this.photoList.clear()
        this.photoList.addAll(photoList)
        galleryAdapter.notifyDataSetChanged()
    }
}