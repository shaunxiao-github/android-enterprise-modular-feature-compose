/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Maybe

@Dao
interface SyncStateDao {
    @Query("SELECT longValue FROM sync_state WHERE key = :key LIMIT 1")
    fun getLong(key: String): Maybe<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun put(state: SyncStateEntity)
}
