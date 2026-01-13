/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl.data

data class MessageDto(
    val id: String,
    val title: String?,
    val content: String?,
    val timestampMillis: Long?,
    val updatedAtMillis: Long,
    val deleted: Boolean
)

data class MessageDeltaDto(
    val serverTimeMillis: Long,
    val messages: List<MessageDto>
)
