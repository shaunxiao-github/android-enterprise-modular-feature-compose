/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.app.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.di.ServiceLocator
import com.example.core.common.DisposableBag
import com.example.core.navigation.FeatureRegistry
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Content(val rows: List<HomeRow>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel : ViewModel() {

    val state = MutableLiveData<HomeUiState>(HomeUiState.Loading)
    private val bag = DisposableBag()

    fun load(context: Context) {
        state.value = HomeUiState.Loading

        val d = ServiceLocator.configRepository(context)
            .subscribeOn(Schedulers.io())
            .flatMap { repo -> repo.homeFeatureIds() }
            .map { ids ->
                ids.mapNotNull { id ->
                    val entry = FeatureRegistry.get(id) ?: return@mapNotNull null
                    HomeRow(
                        featureId = entry.featureId,
                        title = context.getString(entry.titleRes),
                        message = context.getString(entry.messageRes),
                        iconRes = entry.iconRes
                    )
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ rows ->
                state.value = HomeUiState.Content(rows)
            }, { e ->
                state.value = HomeUiState.Error(e.message ?: "Unknown error")
            })

        bag.add(d)
    }

    override fun onCleared() {
        super.onCleared()
        bag.clear()
    }
}
