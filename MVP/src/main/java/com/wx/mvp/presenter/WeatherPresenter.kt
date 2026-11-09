/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wx.mvp.presenter

import com.wx.mvp.model.WeatherModel
import com.wx.mvp.view.WeatherView
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * MVP：Presenter 持有 Model 和 View 接口，不依赖 Android 框架。
 * [attach] 绑定 View，[detach] 取消进行中的请求并释放 View 引用，避免泄漏。
 *
 * @param dispatcher 回调到 View 的调度器；Activity 里传主线程，测试里传测试调度器
 */
class WeatherPresenter(
    private val model: WeatherModel,
    dispatcher: CoroutineDispatcher,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var view: WeatherView? = null
    private var job: Job? = null

    fun attach(view: WeatherView) {
        this.view = view
    }

    fun detach() {
        job?.cancel()
        job = null
        view = null
    }

    fun getWeather(city: String) {
        job?.cancel()
        view?.showLoading()
        job = scope.launch {
            model.getWeather(city).fold(
                onSuccess = { view?.showWeather(it) },
                onFailure = { view?.showError() },
            )
        }
    }

    /** 彻底销毁（Activity.onDestroy）：先解绑再取消整个作用域 */
    fun destroy() {
        detach()
        scope.cancel()
    }
}
