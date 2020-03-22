/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.young.timber.lastfmapi;

import android.content.Context;

import com.young.timber.utils.PreferencesUtility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

public class RestServiceFactory {
    private static final String TAG_OK_HTTP = "OkHttp";
    private static final long CACHE_SIZE = 1024 * 1024;

    public static <T> T createStatic(final Context context, String baseUrl, Class<T> clazz) {
        final OkHttpClient.Builder builder1 = new OkHttpClient.Builder();
        builder1.cache(new Cache(context.getApplicationContext().getCacheDir(),
                CACHE_SIZE));
        builder1.connectTimeout(40, TimeUnit.SECONDS);
        Interceptor interceptor = new Interceptor() {
            PreferencesUtility prefs = PreferencesUtility.getInstance(context);

            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Connection", "keep-alive")
                        .addHeader("Cache-Control",
                                String.format("max-age=%d,%smax-stale=%d",
                                        Integer.valueOf(60 * 60 * 24 * 7),
                                        prefs.loadArtistAndAlbumImages() ? "" : "only-if-cached,", Integer.valueOf(31536000)))
                        .build();
                return chain.proceed(request);
            }
        };
        builder1.addInterceptor(interceptor);
        final OkHttpClient okHttpClient = builder1.build();

        return new Retrofit.Builder().client(okHttpClient).baseUrl(baseUrl).build().create(clazz);
    }

    public static <T> T create(final Context context, String baseUrl, Class<T> clazz) {

        return new Retrofit.Builder().baseUrl(baseUrl).build().create(clazz);

    }


}
