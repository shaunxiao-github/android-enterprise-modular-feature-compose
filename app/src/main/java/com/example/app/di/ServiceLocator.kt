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
import com.example.core.network.NetworkRuntime
import com.example.core.network.RetrofitFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit


object ServiceLocator {

    @Volatile private var retrofit: Retrofit? = null

    fun init(context: Context) {
        // Initialize base URL once per process.
        when (BuildConfig.FLAVOR) {
            "mock" -> {
                // Start mock server in the background and update runtime baseUrl when ready.
                MockServerHolder.ensureStartedAsync()
                    .subscribeOn(Schedulers.io())
                    .subscribe({ info ->
                        NetworkRuntime.setBaseUrl(info.baseUrl)
                        retrofit = null // reset so next call recreates Retrofit with mock url
                    }, {
                        // If mock server fails, fall back to example.com (app still runs offline for cached data)
                        NetworkRuntime.setBaseUrl("https://example.com/")
                    })
            }
            "qa", "prod" -> NetworkRuntime.setBaseUrl("https://example.com/")
            else -> NetworkRuntime.setBaseUrl("https://example.com/")
        }
    }

    fun configRepository(context: Context): Single<ConfigRepository> {
        return Single.fromCallable {
            val r = retrofit ?: createRetrofit(NetworkRuntime.baseUrl).also { retrofit = it }
            ConfigRepository(r.create(ConfigApi::class.java))
        }.subscribeOn(Schedulers.io())
    }

    fun retrofit(): Retrofit =
        retrofit ?: createRetrofit(NetworkRuntime.baseUrl).also { retrofit = it }

    private fun createRetrofit(baseUrl: String): Retrofit =
        RetrofitFactory.create(NetworkConfig(baseUrl), isDebug = BuildConfig.DEBUG)
}
