/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl

import android.content.Context
import android.content.Intent
import com.example.core.navigation.FeatureEntry
import com.example.feature.messagecentre.api.MessageCentreFeature

class MessageCentreEntry : FeatureEntry {
    override val featureId: String = MessageCentreFeature.ID
    override val titleRes: Int = R.string.mc_title
    override val messageRes: Int = R.string.mc_message
    override val iconRes: Int = R.drawable.ic_message_centre

    override fun intent(context: Context): Intent =
        Intent(context, MessageCentreListActivity::class.java)
}
