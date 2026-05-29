package com.yuricunha.yumusic

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.yuricunha.yumusic.di.SubsonicAuthInterceptor
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class YuMusicApp : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var subsonicAuthInterceptor: SubsonicAuthInterceptor

    override fun newImageLoader(context: android.content.Context): ImageLoader {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(subsonicAuthInterceptor)
            .build()

        return ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = okHttpClient,
                    )
                )
            }
            .build()
    }
}
