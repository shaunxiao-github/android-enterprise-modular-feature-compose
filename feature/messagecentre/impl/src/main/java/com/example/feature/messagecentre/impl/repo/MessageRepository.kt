/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
package com.example.feature.messagecentre.impl.repo

import com.example.feature.messagecentre.impl.data.MessageApi
import com.example.feature.messagecentre.impl.local.MessageCentreDb
import com.example.feature.messagecentre.impl.local.MessageEntity
import com.example.feature.messagecentre.impl.local.SyncStateEntity
import com.example.feature.messagecentre.impl.domain.Message
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

class MessageRepository(
    private val api: MessageApi,
    private val db: MessageCentreDb
) {
    private val messageDao = db.messageDao()
    private val syncDao = db.syncStateDao()

    private val KEY_SINCE = "messages_since"

    fun observeMessages(): Flowable<List<Message>> =
        messageDao.observeMessages()
            .map { list -> list.mapNotNull { it.toDomain() } }

    fun observeMessage(id: String): Flowable<Message?> =
        messageDao.observeMessage(id)
            .map { it?.toDomain() }

    /**
     * Incremental sync: GET /messages?since=<watermark>
     *
     * Watermark advances using serverTimeMillis after a successful local transaction,
     * to avoid missing updates when multiple items share the same updatedAtMillis.
     */
    fun syncIncremental(): Completable {
        return syncDao.getLong(KEY_SINCE)
            .defaultIfEmpty(0L)
            .toSingle()
            .flatMap { since -> api.getDelta(since = since) }
            .flatMapCompletable { resp ->
                Completable.fromAction {
                    val entities = resp.messages.map { dto ->
                        MessageEntity(
                            id = dto.id,
                            title = dto.title,
                            content = dto.content,
                            timestampMillis = dto.timestampMillis,
                            updatedAtMillis = dto.updatedAtMillis,
                            isDeleted = dto.deleted
                        )
                    }

                    db.runInTransaction {
                        messageDao.upsertAll(entities)
                        syncDao.put(SyncStateEntity(KEY_SINCE, resp.serverTimeMillis))
                    }
                }
            }
            .subscribeOn(Schedulers.io())
    }

    /**
     * Optimistic delete: mark local deleted immediately, then try server delete.
     * If the network fails, we keep the local deletion (eventual consistency).
     */
    fun deleteMessage(id: String): Completable {
        val now = System.currentTimeMillis()
        return Completable.fromAction {
            db.runInTransaction {
                messageDao.markDeleted(id, updatedAtMillis = now)
            }
        }.andThen(
            api.deleteMessage(id)
                .onErrorComplete()
        ).subscribeOn(Schedulers.io())
    }
}
