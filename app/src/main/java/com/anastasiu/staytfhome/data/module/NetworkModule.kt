package com.anastasiu.staytfhome.data.module

import com.anastasiu.staytfhome.app.StayTFHomeApplication
import com.anastasiu.staytfhome.data.utils.interceptors.AddAuthenticatorCookieInterceptor
import com.anastasiu.staytfhome.data.utils.interceptors.AddMiscHeadersInterceptor
import com.anastasiu.staytfhome.data.utils.interceptors.HeaderApiKeyInterceptor
import com.anastasiu.staytfhome.data.utils.interceptors.GetAuthenticatorCookieInterceptor
import com.anastasiu.staytfhome.utils.NETWORK_CONNECT_TIMEOUT_MS
import com.anastasiu.staytfhome.utils.NETWORK_READ_TIMEOUT_MS
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Module
class NetworkModule(private val application: StayTFHomeApplication) {
    @Provides
    fun provideMainOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(NETWORK_CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .readTimeout(NETWORK_READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .addInterceptor(HeaderApiKeyInterceptor())
        .addInterceptor(AddMiscHeadersInterceptor())
        .addInterceptor(
            AddAuthenticatorCookieInterceptor(
                application
            )
        )
        .addInterceptor(
            GetAuthenticatorCookieInterceptor(
                application
            )
        )
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

}