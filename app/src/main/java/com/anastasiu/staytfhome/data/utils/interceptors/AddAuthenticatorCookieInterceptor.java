package com.anastasiu.staytfhome.data.utils.interceptors;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AddAuthenticatorCookieInterceptor implements Interceptor {
    public static final String PREF_COOKIES = "PREF_COOKIES";
    private Context context;

    public AddAuthenticatorCookieInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        String storedCookie = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_COOKIES, "");

        // Use the following if you need everything in one line.
        // Some APIs die if you do it differently.
        if (!storedCookie.equals("")) {
            builder.addHeader("Cookie", storedCookie);
        }

        return chain.proceed(builder.build());
    }
}
