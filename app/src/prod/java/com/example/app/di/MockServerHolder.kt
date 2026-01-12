package com.example.app.di

import io.reactivex.Single

object MockServerHolder { data class ServerInfo(val baseUrl: String); fun ensureStartedAsync(): Single<ServerInfo> = Single.just(ServerInfo("https://example.com/")) }
