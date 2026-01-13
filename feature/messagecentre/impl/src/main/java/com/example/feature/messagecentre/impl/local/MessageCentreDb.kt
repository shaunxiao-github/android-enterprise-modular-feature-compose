/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MessageEntity::class, SyncStateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MessageCentreDb : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun syncStateDao(): SyncStateDao
}
