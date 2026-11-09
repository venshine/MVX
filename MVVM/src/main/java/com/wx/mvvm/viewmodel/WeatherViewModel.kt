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
package com.wx.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wx.mvvm.model.WeatherModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MVVM：ViewModel 暴露 [StateFlow]，View 订阅它；ViewModel 不持有 View 和 Context。
 * 配置变化（旋转）后 ViewModel 保留，状态不丢。
 */
class WeatherViewModel(private val model: WeatherModel) : ViewModel() {

    private val _state = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private var job: Job? = null

    fun query(city: String) {
        job?.cancel()
        job = viewModelScope.launch {
            _state.value = WeatherUiState.Loading
            _state.value = model.getWeather(city).fold(
                onSuccess = { WeatherUiState.Success(it) },
                onFailure = { WeatherUiState.Error },
            )
        }
    }
}
