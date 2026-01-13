/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApi {
    @GET("messages")
    fun getDelta(
        @Query("since") since: Long,
        @Query("limit") limit: Int = 200
    ): Single<MessageDeltaDto>

    @DELETE("messages/{id}")
    fun deleteMessage(@Path("id") id: String): Completable
}
