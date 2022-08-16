package com.np.suprimpoudel.mayalu.features.shared.repository.base

import com.np.suprimpoudel.mayalu.utils.util.GlobalUtil
import okhttp3.RequestBody

abstract class BaseRepository {
    fun getGlobalUtils(any: Any): RequestBody =
        GlobalUtil.buildGson(any)
}