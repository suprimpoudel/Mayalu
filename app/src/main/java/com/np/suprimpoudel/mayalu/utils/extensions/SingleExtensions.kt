package com.np.suprimpoudel.mayalu.utils.extensions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.np.suprimpoudel.mayalu.R

fun View.hideKeyboard() {
    val imm = ContextCompat.getSystemService(
        context,
        InputMethodManager::class.java
    ) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun setUpDialog(context: Context): Dialog {
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
    return dialog
}

fun getNoInternetDialog(context: Context): AlertDialog {
    val builder = AlertDialog.Builder(context)
    val view = View.inflate(context, R.layout.dialog_no_internet, null)

    builder.setView(view)
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false)
    alertDialog.show()
    alertDialog.window?.setGravity(Gravity.CENTER)
    return alertDialog
}

fun Context.showToast(context: Context?, message: String?) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Activity.hideBottomNavView() {
    findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
}

fun Activity.showBottomNavView() {
    findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
}