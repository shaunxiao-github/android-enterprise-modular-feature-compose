/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.feature.payments.impl

import android.content.Context
import android.content.Intent
import com.example.core.navigation.FeatureEntry
import com.example.feature.payments.api.PaymentsFeature

class PaymentsEntry : FeatureEntry {
    override val featureId: String = PaymentsFeature.ID
    override val titleRes: Int = R.string.payments_title
    override val messageRes: Int = R.string.payments_message
    override val iconRes: Int = R.drawable.ic_payments

    override fun intent(context: Context): Intent =
        Intent(context, PaymentsActivity::class.java)
}
