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
package com.wx.mvi.mvi

import com.wx.mvx.core.model.entity.WeatherInfo

/** 状态变化的原因：由 ViewModel 在处理 Intent 的过程中产生，只交给 Reducer */
sealed interface WeatherChange {
    data object Loading : WeatherChange
    data class Loaded(val info: WeatherInfo) : WeatherChange
    data object Failed : WeatherChange
}

/**
 * 纯函数 Reducer：(旧状态, 变化) -> 新状态。
 * 不读时间、不碰 Android、不做 IO，同样的输入永远得到同样的输出，所以不用协程就能测。
 */
object WeatherReducer {
    fun reduce(state: WeatherState, change: WeatherChange): WeatherState = when (change) {
        WeatherChange.Loading -> state.copy(status = Status.LOADING, info = null)
        is WeatherChange.Loaded -> state.copy(status = Status.SUCCESS, info = change.info)
        WeatherChange.Failed -> state.copy(status = Status.ERROR, info = null)
    }
}
