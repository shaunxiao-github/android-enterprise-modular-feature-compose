/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.core.config

import io.reactivex.Single
import retrofit2.http.GET

interface ConfigApi {
    @GET("config/home")
    fun getHomeConfig(): Single<HomeConfigDto>
}
