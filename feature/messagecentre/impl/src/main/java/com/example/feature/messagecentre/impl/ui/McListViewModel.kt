/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */
 
package com.example.feature.messagecentre.impl.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.feature.messagecentre.impl.domain.Message
import com.example.feature.messagecentre.impl.repo.MessageRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

data class MessageListItem(
    val id: String,
    val title: String,
    val timestampMillis: Long
)

sealed class McListState {
    data object Loading : McListState()
    data class Content(val items: List<MessageListItem>) : McListState()
    data class Error(val message: String) : McListState()
}

class McListViewModel(
    private val repo: MessageRepository
) : ViewModel() {

    val state = MutableLiveData<McListState>(McListState.Loading)
    val transientError = MutableLiveData<String?>(null)

    private val bag = CompositeDisposable()

    fun start() {
        bag.add(
            repo.observeMessages()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    state.value = McListState.Content(
                        list.map { it.toListItem() }
                    )
                }, { e ->
                    state.value = McListState.Error(e.message ?: "Unknown error")
                })
        )

        // initial sync
        refresh()
    }

    fun refresh() {
        bag.add(
            repo.syncIncremental()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // success: UI will update through the local DB stream
                }, { e ->
                    // keep showing cached local data; just surface an error message
                    transientError.value = e.message ?: "Sync failed"
                })
        )
    }

    override fun onCleared() {
        bag.clear()
    }
}

private fun Message.toListItem() = MessageListItem(
    id = id,
    title = title,
    timestampMillis = timestampMillis
)
