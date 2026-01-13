/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl.domain

data class Message(
    val id: String,
    val title: String,
    val content: String,
    val timestampMillis: Long
)
