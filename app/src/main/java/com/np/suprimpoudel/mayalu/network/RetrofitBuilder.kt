package com.np.suprimpoudel.mayalu.network

import com.google.gson.GsonBuilder
import com.np.suprimpoudel.mayalu.utils.constants.APIConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitBuilder {
    companion object {
        private var apiService: ApiService? = null

        private val gson = GsonBuilder().setLenient().create()

        private fun getOkHttpClient(): OkHttpClient {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(getHeaderInterceptor())
                .addInterceptor(getContentTypeInterceptor())
            return okHttpClient.build()
        }

        private fun getHeaderInterceptor(): Interceptor {
            return Interceptor { chain ->
                val request =
                    chain.request().newBuilder()
                        .header("Authorization", APIConstants.SERVER_KEY)
                        .build()
                chain.proceed(request)
            }
        }

        private fun getContentTypeInterceptor(): Interceptor {
            return Interceptor { chain ->
                val request =
                    chain.request().newBuilder()
                        .header("Content-Type", APIConstants.CONTENT_TYPE)
                        .build()
                chain.proceed(request)
            }
        }

        private val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(APIConstants.BASE_URL)
            .client(getOkHttpClient())
            .build()

        fun getApiService(): ApiService {
            return apiService ?: retrofit.create(ApiService::class.java)
        }
    }
}