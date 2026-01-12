/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.core.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun EnterpriseButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(modifier = modifier, onClick = onClick) { Text(text) }
}
