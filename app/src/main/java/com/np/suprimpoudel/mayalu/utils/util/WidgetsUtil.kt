package com.np.suprimpoudel.mayalu.utils.util

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.setCustomError(errorMessage: String) {
    this.error = errorMessage
}

fun TextInputLayout.removeError() {
    this.error = null
    this.isErrorEnabled = false
}