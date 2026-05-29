package com.yuricunha.yumusic.di

import com.yuricunha.yumusic.data.api.SubsonicApiService
import com.yuricunha.yumusic.data.repository.ServerConfig
import com.yuricunha.yumusic.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderModule {

    @Provides
    @Singleton
    fun provideSubsonicAuthInterceptor(
        settingsRepository: SettingsRepository,
    ): SubsonicAuthInterceptor {
        return SubsonicAuthInterceptor(settingsRepository)
    }
}

/**
 * Interceptor that adds Subsonic auth params to image/stream requests.
 * Used by Coil's OkHttp client configured in YuMusicApp (SingletonImageLoader.Factory).
 * Uses @Volatile cached config to avoid blocking OkHttp threads.
 */
class SubsonicAuthInterceptor @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : Interceptor {

    @Volatile
    private var cachedConfig: ServerConfig = ServerConfig()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        scope.launch {
            settingsRepository.serverConfig.collect { config ->
                cachedConfig = config
            }
        }
    }

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val url = originalRequest.url
        val path = url.encodedPath

        if (path.contains("/rest/getCoverArt") || path.contains("/rest/stream")) {
            val config = cachedConfig
            if (config.isConfigured) {
                val newUrl = url.newBuilder()
                    .addQueryParameter("u", config.username)
                    .addQueryParameter("p", config.password)
                    .addQueryParameter("v", SubsonicApiService.API_VERSION)
                    .addQueryParameter("c", SubsonicApiService.CLIENT_NAME)
                    .build()
                val newRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build()
                return chain.proceed(newRequest)
            }
        }
        return chain.proceed(originalRequest)
    }
}
