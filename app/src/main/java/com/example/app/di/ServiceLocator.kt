/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.app.di

import android.content.Context
import com.example.app.BuildConfig
import com.example.core.config.ConfigApi
import com.example.core.config.ConfigRepository
import com.example.core.network.NetworkConfig
import com.example.core.network.RetrofitFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

object ServiceLocator {

    fun configRepository(context: Context): Single<ConfigRepository> {
        return when (BuildConfig.FLAVOR) {
            "mock" -> MockServerHolder.ensureStartedAsync()
                .map { it.baseUrl }
                .map { baseUrl ->
                    val retrofit = createRetrofit(baseUrl)
                    ConfigRepository(retrofit.create(ConfigApi::class.java))
                }
            "qa", "prod" -> Single.fromCallable {
                val retrofit = createRetrofit("https://example.com/")
                ConfigRepository(retrofit.create(ConfigApi::class.java))
            }.subscribeOn(Schedulers.io())
            else -> Single.fromCallable {
                val retrofit = createRetrofit("https://example.com/")
                ConfigRepository(retrofit.create(ConfigApi::class.java))
            }.subscribeOn(Schedulers.io())
        }
    }

    private fun createRetrofit(baseUrl: String): Retrofit =
        RetrofitFactory.create(NetworkConfig(baseUrl), isDebug = BuildConfig.DEBUG)

    fun init(context: Context) {
        // no-op (lazy initialization)
    }
}
