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

class McDetailViewModel(
    private val repo: MessageRepository
) : ViewModel() {

    val message = MutableLiveData<Message?>(null)
    val error = MutableLiveData<String?>(null)

    private val bag = CompositeDisposable()

    fun start(id: String) {
        bag.add(
            repo.observeMessage(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ msg ->
                    message.value = msg
                }, { e ->
                    error.value = e.message ?: "Load failed"
                })
        )
    }

    fun delete(id: String, onDone: () -> Unit) {
        bag.add(
            repo.deleteMessage(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onDone()
                }, { e ->
                    error.value = e.message ?: "Delete failed"
                })
        )
    }

    override fun onCleared() {
        bag.clear()
    }
}
