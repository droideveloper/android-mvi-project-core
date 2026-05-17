package com.mvi.core.network

import com.mvi.core.environment.Environment
import com.mvi.core.network.adapter.SuspendResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlin.time.Duration.Companion.seconds

@Module
interface NetworkModule {

    companion object {

        @Provides
        fun provideLoggInterceptor(env: Environment): Interceptor =
            HttpLoggingInterceptor().setLevel(
                level = when {
                    env.isDebug -> HttpLoggingInterceptor.Level.BODY
                    else -> HttpLoggingInterceptor.Level.NONE
                }
            )

        @Provides
        fun provideHttpClient(interceptor: Interceptor): OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(60.seconds)
                .readTimeout(30.seconds)
                .writeTimeout(30.seconds)
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor)
                .build()

        @Provides
        fun provideRetrofitBuilder(client: OkHttpClient): Retrofit.Builder {
            val json = Json {
                ignoreUnknownKeys = true
            }
            return Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(SuspendResultCallAdapterFactory())
                .addConverterFactory(
                    json.asConverterFactory(
                        contentType = "application/json; charset=UTF-8".toMediaType()
                    )
                )
        }
    }
}
