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

// UI state for the detail screen
sealed class McDetailState {
    data object Loading : McDetailState()
    data class Content(
        val id: String,
        val title: String,
        val content: String,
        val timestampMillis: Long
    ) : McDetailState()

    data class Error(val message: String) : McDetailState()
}

class McDetailViewModel(
    private val repo: MessageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<McDetailState>(McDetailState.Loading)
    val state: StateFlow<McDetailState> = _state.asStateFlow()

    // one-shot error for snackbar / toast
    private val _transientError = MutableStateFlow<String?>(null)
    val transientError: StateFlow<String?> = _transientError.asStateFlow()

    private val bag = DisposableBag()
    private var currentId: String? = null

    fun start(id: String) {
        currentId = id

        bag.add(
            repo.observeMessage(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ msg ->
                    msg?.let {
                        _state.value = McDetailState.Content(
                            id = it.id,
                            title = it.title,
                            content = it.content,
                            timestampMillis = it.timestampMillis
                        )
                    }
                }, { e ->
                    _state.value = McDetailState.Error(
                        e.message ?: "Failed to load message"
                    )
                })
        )
    }

    fun delete(onDeleted: () -> Unit) {
        val id = currentId ?: return

        bag.add(
            repo.deleteMessage(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        // We let the Activity decide what to do (usually finish())
                        onDeleted()
                    },
                    { e ->
                        _transientError.value = e.message ?: "Delete failed"
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

