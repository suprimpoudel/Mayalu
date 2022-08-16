package com.np.suprimpoudel.mayalu.features.ui.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentUserDetailsBinding
import jp.wasabeef.glide.transformations.BlurTransformation

class UserDetailFragment : BottomSheetDialogFragment() {

    private var binding: FragmentUserDetailsBinding? = null
    private val args by navArgs<UserDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserDetailsBinding.bind(view)

        initListener()
        populateData(view)
    }

    private fun initListener() {
        binding?.imvBackIcon?.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun populateData(view: View) {
        binding?.imvBackgroundImage?.let {
            Glide.with(view)
                .load(args.profileImageUrl)
                .error(R.drawable.mayalu_app_icon)
                //Blur effect for background image
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .placeholder(R.drawable.mayalu_app_icon)
                .into(it)
        }

        binding?.imvProfilePicture?.let {
            Glide.with(view)
                .load(args.profileImageUrl)
                .error(R.drawable.mayalu_app_icon)
                .placeholder(R.drawable.mayalu_app_icon)
                .into(it)
        }

        binding?.tvxUserName?.text = args.username
        binding?.txvAgeGender?.text = "${args.gender}, ${args.age}"
        binding?.txvAbout?.text = args.about
    }
}