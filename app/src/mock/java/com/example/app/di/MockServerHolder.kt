/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.app.di

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * MockWebServer holder for the mockDebug flavor.
 *
 * Endpoints:
 * - GET  /config/home
 * - GET  /messages?since=<millis>
 * - DELETE /messages/{id}
 */
object MockServerHolder {

    data class ServerInfo(val baseUrl: String)

    private data class ServerMessage(
        val id: String,
        var title: String,
        var content: String,
        var timestampMillis: Long,
        var updatedAtMillis: Long,
        var deleted: Boolean
    )

    @Volatile private var server: MockWebServer? = null
    private val started = AtomicBoolean(false)

    // in-memory "database"
    private val lock = Any()
    private val messages: MutableList<ServerMessage> = mutableListOf()

    fun ensureStartedAsync(): Single<ServerInfo> =
        Single.fromCallable {
            if (started.get() && server != null) {
                return@fromCallable ServerInfo(server!!.url("/").toString())
            }

            val s = MockWebServer()

            // seed data
            val now = System.currentTimeMillis()
            synchronized(lock) {
                messages.clear()
                messages += ServerMessage(
                    id = "m1",
                    title = "Welcome",
                    content = "Thanks for installing the app.",
                    timestampMillis = now - 2 * 24 * 60 * 60 * 1000L,
                    updatedAtMillis = now - 2 * 24 * 60 * 60 * 1000L,
                    deleted = false
                )
                messages += ServerMessage(
                    id = "m2",
                    title = "Promo",
                    content = "Your rewards are ready.",
                    timestampMillis = now - 24 * 60 * 60 * 1000L,
                    updatedAtMillis = now - 24 * 60 * 60 * 1000L,
                    deleted = false
                )
            }

            s.dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    val path = request.requestUrl?.encodedPath ?: request.path.orEmpty()
                    return when {
                        request.method == "GET" && path == "/config/home" -> {
                            jsonResponse(
                                200,
                                // include message centre
                                """{"home":["payments","profile","message_centre"]}"""
                            )
                        }

                        request.method == "GET" && path == "/messages" -> {
                            val since = request.requestUrl?.queryParameter("since")?.toLongOrNull() ?: 0L
                            val serverTime = System.currentTimeMillis()
                            val delta = synchronized(lock) {
                                messages
                                    .filter { it.updatedAtMillis > since }
                                    .map {
                                        mapOf(
                                            "id" to it.id,
                                            "title" to it.title,
                                            "content" to it.content,
                                            "timestampMillis" to it.timestampMillis,
                                            "updatedAtMillis" to it.updatedAtMillis,
                                            "deleted" to it.deleted
                                        )
                                    }
                            }
                            val body = buildString {
                                append("{")
                                append("\"serverTimeMillis\":").append(serverTime).append(",")
                                append("\"messages\":[")
                                delta.forEachIndexed { idx, m ->
                                    if (idx > 0) append(",")
                                    append("{")
                                    append("\"id\":\"").append(m["id"]).append("\",")
                                    append("\"title\":\"").append(escapeJson(m["title"] as String)).append("\",")
                                    append("\"content\":\"").append(escapeJson(m["content"] as String)).append("\",")
                                    append("\"timestampMillis\":").append(m["timestampMillis"]).append(",")
                                    append("\"updatedAtMillis\":").append(m["updatedAtMillis"]).append(",")
                                    append("\"deleted\":").append(m["deleted"])
                                    append("}")
                                }
                                append("]}")
                            }
                            jsonResponse(200, body)
                        }

                        request.method == "DELETE" && path.startsWith("/messages/") -> {
                            val id = path.removePrefix("/messages/")
                            val serverTime = System.currentTimeMillis()
                            synchronized(lock) {
                                val msg = messages.firstOrNull { it.id == id }
                                if (msg != null) {
                                    msg.deleted = true
                                    msg.updatedAtMillis = serverTime
                                } else {
                                    // create tombstone anyway (server says deleted)
                                    messages += ServerMessage(
                                        id = id,
                                        title = "",
                                        content = "",
                                        timestampMillis = 0L,
                                        updatedAtMillis = serverTime,
                                        deleted = true
                                    )
                                }
                            }
                            MockResponse().setResponseCode(204)
                        }

                        else -> MockResponse().setResponseCode(404)
                    }
                }
            }

            s.start()
            server = s
            started.set(true)
            ServerInfo(s.url("/").toString())
        }.subscribeOn(Schedulers.io())
    private fun jsonResponse(code: Int, body: String): MockResponse =
        MockResponse()
            .setResponseCode(code)
            .addHeader("Content-Type", "application/json")
            .setBody(body)

    private fun escapeJson(s: String): String =
        s.replace("\\", "\\\\").replace("\"", "\\\"")//.replace("", "\\\n")
}
