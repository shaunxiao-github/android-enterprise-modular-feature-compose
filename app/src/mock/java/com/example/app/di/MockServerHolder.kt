/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.app.di

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

object MockServerHolder {

    data class ServerInfo(val baseUrl: String)

    @Volatile private var server: MockWebServer? = null

    fun ensureStartedAsync(): Single<ServerInfo> =
        Single.fromCallable {
            server?.let { return@fromCallable ServerInfo(it.url("/").toString()) }

            val s = MockWebServer()
            val responseJson = """{"home":["payments","profile"]}"""

            repeat(10) {
                s.enqueue(
                    MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody(responseJson)
                )
            }

            s.start()
            server = s
            ServerInfo(s.url("/").toString())
        }.subscribeOn(Schedulers.io())
}
