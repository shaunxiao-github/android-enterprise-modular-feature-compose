/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.core.navigation

import java.util.ServiceLoader

object FeatureRegistry {
    private val entries: Map<String, FeatureEntry> by lazy {
        ServiceLoader.load(FeatureEntry::class.java)
            .iterator()
            .asSequence()
            .associateBy { it.featureId }
    }

    fun get(featureId: String): FeatureEntry? = entries[featureId]
    fun all(): List<FeatureEntry> = entries.values.sortedBy { it.featureId }
}
