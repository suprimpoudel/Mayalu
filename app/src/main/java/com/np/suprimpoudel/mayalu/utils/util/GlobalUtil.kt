package com.np.suprimpoudel.mayalu.utils.util

import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


object GlobalUtil {
    fun buildGson(any: Any): RequestBody {
        val builder = GsonBuilder()
        val gson = builder.create()
        val json = gson.toJson(any)
        return json.toRequestBody("application/json".toMediaTypeOrNull())
    }
}