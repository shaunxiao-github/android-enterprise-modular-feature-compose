/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.core.network

/**
 * A tiny runtime configuration holder.
 *
 * App layer initializes it on startup (e.g., mock server URL vs prod URL),
 * and feature modules can read it without depending on the app module.
 */
object NetworkRuntime {
    @Volatile var baseUrl: String = "https://example.com/"
        private set

    fun setBaseUrl(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
    }
}
