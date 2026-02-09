/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.feature.messagecentre.impl.ui

import androidx.lifecycle.ViewModel
import com.example.core.common.DisposableBag
import com.example.feature.messagecentre.impl.repo.MessageRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class McListState {
    data object Loading : McListState()
    data class Content(val items: List<MessageListItem>) : McListState()
    data class Error(val message: String) : McListState()
}

data class MessageListItem(
    val id: String,
    val title: String,
    val timestampMillis: Long
)

class McListViewModel(
    private val repo: MessageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<McListState>(McListState.Loading)
    val state: StateFlow<McListState> = _state.asStateFlow()

    private val _transientError = MutableStateFlow<String?>(null)
    val transientError: StateFlow<String?> = _transientError.asStateFlow()

    private val bag = DisposableBag()

    fun start() {
        // 1) Observe local DB and push into StateFlow
        bag.add(
            repo.observeMessages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ messages ->
                    val items = messages.map { msg ->
                        MessageListItem(
                            id = msg.id,
                            title = msg.title,
                            timestampMillis = msg.timestampMillis
                        )
                    }
                    _state.value = McListState.Content(items)
                }, { e ->
                    _state.value = McListState.Error(e.message ?: "Unknown error")
                })
        )

        // 2) Trigger incremental sync on start
        refresh()
    }

    fun refresh() {
        bag.add(
            repo.syncIncremental()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { /* success: no-op, DB observer will emit */ },
                    { e ->
                        // Keep current Content; optionally wrap it with error message
                        val current = _state.value
                        if (current is McListState.Content) {
                            // You could add a separate side-effect channel for errors;
                            // for now we just leave state as-is.
                        } else {
                            _transientError.value = e.message ?: "Sync failed"
                        }
                    }
                )
        )
    }

    fun clearTransientError() {
        _transientError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        bag.clear()
    }
}
