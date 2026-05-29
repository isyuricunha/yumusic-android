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
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val DYNAMIC_BASE_URL = "http://placeholder.example.com/"

    @Provides
    @Singleton
    fun provideDynamicBaseUrlInterceptor(
        settingsRepository: SettingsRepository,
    ): DynamicBaseUrlInterceptor {
        return DynamicBaseUrlInterceptor(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(dynamicBaseUrlInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DYNAMIC_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSubsonicApiService(retrofit: Retrofit): SubsonicApiService {
        return retrofit.create(SubsonicApiService::class.java)
    }
}

/**
 * Interceptor that rewrites the placeholder base URL with the actual server URL.
 * Uses @Volatile cached config to avoid blocking OkHttp threads.
 */
class DynamicBaseUrlInterceptor(
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
        val config = cachedConfig

        if (config.isConfigured) {
            val serverUrl = config.serverUrl.trimEnd('/')
            try {
                val newUrl = serverUrl.toHttpUrl()
                val newRequest = originalRequest.newBuilder()
                    .url(
                        originalRequest.url.newBuilder()
                            .scheme(newUrl.scheme)
                            .host(newUrl.host)
                            .port(newUrl.port)
                            .encodedPath(originalRequest.url.encodedPath)
                            .encodedQuery(originalRequest.url.encodedQuery)
                            .build()
                    )
                    .build()
                return chain.proceed(newRequest)
            } catch (_: Exception) {
                // Invalid URL, proceed with original
            }
        }
        return chain.proceed(originalRequest)
    }
}
