/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.feature.profile.impl

import android.content.Context
import android.content.Intent
import com.example.core.navigation.FeatureEntry
import com.example.feature.profile.api.ProfileFeature

class ProfileEntry : FeatureEntry {
    override val featureId: String = ProfileFeature.ID
    override val titleRes: Int = R.string.profile_title
    override val messageRes: Int = R.string.profile_message
    override val iconRes: Int = R.drawable.ic_profile

    override fun intent(context: Context): Intent =
        Intent(context, ProfileActivity::class.java)
}
