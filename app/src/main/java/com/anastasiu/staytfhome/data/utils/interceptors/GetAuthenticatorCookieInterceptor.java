package com.anastasiu.staytfhome.data.utils.interceptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class GetAuthenticatorCookieInterceptor implements Interceptor {
    public static final String PREF_COOKIES = "PREF_COOKIES";

    private Context context;
    public GetAuthenticatorCookieInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            String cookieHeader = originalResponse.header("Set-Cookie");

            SharedPreferences.Editor memes = PreferenceManager.getDefaultSharedPreferences(context).edit();
            memes.remove(PREF_COOKIES).apply();
            memes.putString(PREF_COOKIES, cookieHeader).apply();
            memes.commit();
        }

        return originalResponse;
    }
}
