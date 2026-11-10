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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wx.mvx.core.model.WeatherModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI：ViewModel 持有 [StateFlow]<[WeatherState]>，唯一入口是 [dispatch]。
 * Intent -> 产生 [WeatherChange] -> [WeatherReducer] 算出新状态；一次性事件走 [effects]。
 */
class WeatherViewModel(private val model: WeatherModel) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    // 缓冲 Channel：View 不在前台时事件先存着，回到前台再消费，且只消费一次
    private val _effects = Channel<WeatherEffect>(Channel.BUFFERED)
    val effects: Flow<WeatherEffect> = _effects.receiveAsFlow()

    private var job: Job? = null

    fun dispatch(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.Query -> query(intent)
        }
    }

    private fun query(intent: WeatherIntent.Query) {
        job?.cancel()
        job = viewModelScope.launch {
            reduce(WeatherChange.Loading)
            model.getWeather(intent.city, intent.simulateFailure).fold(
                onSuccess = { reduce(WeatherChange.Loaded(it)) },
                onFailure = {
                    reduce(WeatherChange.Failed)
                    _effects.send(WeatherEffect.ShowFailure)
                },
            )
        }
    }

    private fun reduce(change: WeatherChange) = _state.update { WeatherReducer.reduce(it, change) }
}
