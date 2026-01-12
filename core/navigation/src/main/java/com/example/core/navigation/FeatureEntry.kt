/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.core.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface FeatureEntry {
    val featureId: String
    @get:StringRes val titleRes: Int
    @get:StringRes val messageRes: Int
    @get:DrawableRes val iconRes: Int

    fun intent(context: Context): Intent
}
