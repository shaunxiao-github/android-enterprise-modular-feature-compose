/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.feature.messagecentre.impl.domain.Message

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val title: String?,
    val content: String?,
    val timestampMillis: Long?,
    val updatedAtMillis: Long,
    val isDeleted: Boolean
) {
    fun toDomain(): Message? {
        if (isDeleted) return null
        val t = title ?: return null
        val c = content ?: ""
        val ts = timestampMillis ?: 0L
        return Message(id = id, title = t, content = c, timestampMillis = ts)
    }
}

@Entity(tableName = "sync_state")
data class SyncStateEntity(
    @PrimaryKey val key: String,
    val longValue: Long
)
