package com.np.suprimpoudel.mayalu.utils.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import com.np.suprimpoudel.mayalu.R

object DialogUtil {
    private var dialog: Dialog? = null
    private fun setUpDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_loading_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if (dialog.window != null) {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        }
        this.dialog = dialog
    }

    fun showDialog(context: Context) {
        setUpDialog(context)
        dialog?.show()
    }

    fun hideDialog() {
        dialog?.hide()
    }
}