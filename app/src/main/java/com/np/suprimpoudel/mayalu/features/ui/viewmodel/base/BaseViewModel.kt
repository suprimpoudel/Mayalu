package com.np.suprimpoudel.mayalu.features.ui.viewmodel.base

import androidx.lifecycle.AndroidViewModel
import com.np.suprimpoudel.mayalu.features.application.MyApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(application: MyApplication) :
    AndroidViewModel(application) {
    protected val context
        get() = getApplication<MyApplication>()
}