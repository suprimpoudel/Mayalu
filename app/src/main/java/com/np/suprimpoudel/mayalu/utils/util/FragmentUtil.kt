package com.np.suprimpoudel.mayalu.utils.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

fun Fragment.navigateTo(resId: Int) {
    findNavController().navigate(resId)
}

fun Fragment.navigateWithDirections(directions: NavDirections) {
    findNavController().navigate(directions)
}

fun Fragment.popBack() {
    findNavController().popBackStack()
}

fun Fragment.showToast(toastMessage: String?) {
    Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
}

fun Fragment.showSnackbar(snackbarMessage: String?) {
    snackbarMessage?.let {
        Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
    }
}