package com.np.suprimpoudel.mayalu.features.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.np.suprimpoudel.mayalu.R
import com.np.suprimpoudel.mayalu.databinding.ActivityUserBinding
import com.np.suprimpoudel.mayalu.utils.constants.FirebaseConstant
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    @Named("firebaseDatabase")
    lateinit var firebaseDatabase: FirebaseDatabase

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setUpNavigationComponent()
        checkIfDataUploaded()
        destinationChangeListener()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setUpNavigationComponent() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        AppBarConfiguration(navController.graph)
        binding.bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController)
    }

    private fun checkIfDataUploaded() {
        val userRef =
            firebaseDatabase.getReference(FirebaseConstant.DATA_USERS)
        val checkIfUserIsVerified: Query =
            userRef.orderByChild(firebaseAuth.uid ?: "")
        checkIfUserIsVerified.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.child(firebaseAuth.uid ?: "").child("uid").exists()) {
                        closeActivity()
                    }
                } else {
                    closeActivity()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                closeActivity()
            }
        })

    }

    private fun closeActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun destinationChangeListener() {
        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?,
            ) {
                when(destination.id) {
                    R.id.swipeFragment -> hideActionBar()
                    R.id.userDetailFragment -> hideActionBar()
                    R.id.chatFragment -> hideActionBar()
                    else -> showActionBar()
                }
            }
        })
    }

    private fun showActionBar() {
        binding.toolbarMain.isVisible = true
    }

    private fun hideActionBar() {
        binding.toolbarMain.isVisible = false
    }
}