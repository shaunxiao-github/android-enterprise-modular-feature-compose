/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.core.config

import io.reactivex.Single

class ConfigRepository(private val api: ConfigApi) {
    fun homeFeatureIds(): Single<List<String>> = api.getHomeConfig().map { it.home }
}
