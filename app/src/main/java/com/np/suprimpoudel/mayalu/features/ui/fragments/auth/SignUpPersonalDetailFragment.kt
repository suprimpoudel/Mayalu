package com.np.suprimpoudel.mayalu.features.ui.fragments.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.FragmentSignUpPersonalDetailBinding
import com.np.suprimpoudel.mayalu.utils.util.navigateWithDirections
import com.np.suprimpoudel.mayalu.utils.util.showToast
import java.text.SimpleDateFormat
import java.util.*


class SignUpPersonalDetailFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSignUpPersonalDetailBinding
    private var cal: Calendar = Calendar.getInstance()
    private var age = 0

    private val args by navArgs<SignUpPersonalDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_sign_up_personal_detail,
            container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.btnAge.text = sdf.format(cal.time)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                age = getAge(year, monthOfYear, dayOfMonth)
                updateDateInView()
            }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.maxDate = cal.timeInMillis - 1000

        binding.btnAge.setOnClickListener {
            datePickerDialog.show()
        }
        initListener()
    }

    private fun initListener() {
        binding.btnContinue.setOnClickListener(this)
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.btnAge.text = sdf.format(cal.time)
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        return age
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.btnContinue.id -> validateInput()
        }
    }

    private fun validateInput() {
        if (age < 18) {
            showToast("You must be at least 18 years old")
            return
        }
        if (binding.edtInputName.text.toString().isEmpty()) {
            showToast("Please enter your name")
            return
        }
        val direction =
            SignUpPersonalDetailFragmentDirections.actionSignUpPersonalDetailFragmentToSignUpUploadPhotoFragment(
                gender = args.gender,
                lookingFor = args.lookingFor,
                age = age,
                name = binding.edtInputName.text.toString().trim()
            )
        navigateWithDirections(direction)
    }
}