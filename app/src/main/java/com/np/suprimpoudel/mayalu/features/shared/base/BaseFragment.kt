package com.np.suprimpoudel.mayalu.features.shared.base

import android.app.Dialog
import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.np.suprimpoudel.mayalu.utils.extensions.setUpDialog

open class BaseFragment : Fragment() {

    private var dialog: Dialog? = null

    protected fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showSnackbar(message: String?) {
        Snackbar.make(requireView(), message.toString(), Snackbar.LENGTH_SHORT).show()
    }

    protected fun initDialog() {
        dialog = setUpDialog(requireContext())
    }

    protected fun showDialog() {
        initDialog()
        dialog?.show()
    }

    protected fun dismissDialog() {
        dialog?.dismiss()
    }

    protected fun navigateTo(fragmentId: Int) {
        findNavController().navigate(fragmentId)
    }

    protected fun popBack() {
        findNavController().popBackStack()
    }

    protected fun navigateWithArgs(navDirection: NavDirections) {
        findNavController().navigate(navDirection)
    }

    protected fun isValidEmail(emailInput: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()
    }

    protected fun setError(textInputLayout: TextInputLayout?, errorMessage: String) {
        textInputLayout?.error = errorMessage
    }

    protected fun removeError(textInputLayout: TextInputLayout?) {
        textInputLayout?.error = null
        textInputLayout?.isErrorEnabled = false
    }
}