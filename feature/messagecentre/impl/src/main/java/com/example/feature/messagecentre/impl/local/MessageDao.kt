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
import io.reactivex.Flowable

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE isDeleted = 0 ORDER BY timestampMillis DESC")
    fun observeMessages(): Flowable<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE id = :id LIMIT 1")
    fun observeMessage(id: String): Flowable<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(items: List<MessageEntity>)

    @Query("UPDATE messages SET isDeleted = 1, updatedAtMillis = :updatedAtMillis WHERE id = :id")
    fun markDeleted(id: String, updatedAtMillis: Long)

    @Query("DELETE FROM messages WHERE id = :id")
    fun hardDelete(id: String)
}
