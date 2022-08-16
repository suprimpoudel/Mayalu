package com.np.suprimpoudel.mayalu.utils.util

import android.view.View
import com.np.suprimpoudel.mayalu.databinding.ItemProgressButtonBinding

fun ItemProgressButtonBinding.showProgress() {
    this.prbButton.visibility = View.VISIBLE
}

fun ItemProgressButtonBinding.hideProgress() {
    this.prbButton.visibility = View.GONE
}