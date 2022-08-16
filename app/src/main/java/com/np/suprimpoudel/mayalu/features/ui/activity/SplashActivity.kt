package com.np.suprimpoudel.mayalu.features.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant
import com.np.suprimpoudel.mayalu.utils.extensions.getNoInternetDialog
import com.np.suprimpoudel.mayalu.utils.extensions.hasInternetConnection
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("firebaseDatabase")
    lateinit var firebaseDatabase: FirebaseDatabase

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        hideSystemUI()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initActivity()
    }

    private fun initActivity() {
        if (hasInternetConnection()) {
            checkIfSignedIn()
        } else {
            val alertDialog = getNoInternetDialog(this)
            val refreshButton = alertDialog.findViewById<TextView>(R.id.txvRefreshBtn)
            refreshButton?.setOnClickListener {
                alertDialog.dismiss()
                initActivity()
            }
        }
    }

    private fun checkIfSignedIn() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            checkIfDataUploaded(user.uid, user.email ?: "")
        } else {
            redirectToLoginScreen()
        }
    }

    private fun checkIfDataUploaded(userId: String, email: String) {
        val userRef =
            firebaseDatabase.getReference(FirebaseConstant.DATA_USERS)
        val checkIfUserIsVerified: Query =
            userRef.orderByChild(userId)
        checkIfUserIsVerified.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isUserIsVerified: Boolean =
                        snapshot.child(userId).child(FirebaseConstant.IS_VERIFIED).value as Boolean
                    if (isUserIsVerified) {
                        if (snapshot.child(userId).child("uid").exists()) {
                            redirectToUserDashboard()
                        } else {
                            redirectToLoginScreen()
                        }
                    } else {
                        redirectToLoginScreen()
                    }
                } else {
                    showToast("Some Error Occurred")
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast(error.message)
                redirectToLoginScreen()
            }
        })

    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun redirectToLoginScreen() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun redirectToUserDashboard() {
        startActivity(Intent(this, UserActivity::class.java))
        finish()
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView)

        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
    }

}