/*
 * Copyright (c) 2025 Shaun Xiao
 *
 * Licensed under the MIT License.
 * See the LICENSE file in the project root for license information.
 */

package com.example.core.common

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class DisposableBag {
    private val cd = CompositeDisposable()
    fun add(d: Disposable) { cd.add(d) }
    fun clear() { cd.clear() }
}
