package com.anastasiu.staytfhome.data.utils.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AddMiscHeadersInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .build();

        return chain.proceed(request)
    }
}