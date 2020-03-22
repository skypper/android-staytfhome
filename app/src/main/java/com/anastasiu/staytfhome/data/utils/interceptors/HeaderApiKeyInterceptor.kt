package com.anastasiu.staytfhome.data.utils.interceptors

import com.anastasiu.staytfhome.utils.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class HeaderApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request().newBuilder()
            .addHeader("X-ApiKey", API_KEY)
            .build();

        return chain.proceed(request)
    }
}
