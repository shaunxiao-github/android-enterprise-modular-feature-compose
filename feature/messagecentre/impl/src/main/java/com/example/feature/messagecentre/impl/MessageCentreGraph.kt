/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl

import android.content.Context
import androidx.room.Room
import com.example.core.network.NetworkConfig
import com.example.core.network.NetworkRuntime
import com.example.core.network.RetrofitFactory
import com.example.feature.messagecentre.impl.data.MessageApi
import com.example.feature.messagecentre.impl.local.MessageCentreDb
import com.example.feature.messagecentre.impl.repo.MessageRepository
import retrofit2.Retrofit

object MessageCentreGraph {

    @Volatile private var db: MessageCentreDb? = null
    @Volatile private var retrofit: Retrofit? = null
    @Volatile private var repo: MessageRepository? = null

    fun repository(context: Context): MessageRepository =
        repo ?: synchronized(this) {
            repo ?: MessageRepository(
                api = retrofit(context).create(MessageApi::class.java),
                db = database(context)
            ).also { repo = it }
        }

    private fun database(context: Context): MessageCentreDb =
        db ?: synchronized(this) {
            db ?: Room.databaseBuilder(
                context.applicationContext,
                MessageCentreDb::class.java,
                "messagecentre.db"
            ).build().also { db = it }
        }

    private fun retrofit(context: Context): Retrofit =
        retrofit ?: synchronized(this) {
            retrofit ?: RetrofitFactory.create(
                NetworkConfig(NetworkRuntime.baseUrl),
                isDebug = BuildConfig.DEBUG
            ).also { retrofit = it }
        }
}
