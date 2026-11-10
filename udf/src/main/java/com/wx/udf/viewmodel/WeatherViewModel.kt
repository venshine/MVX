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
package com.wx.udf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wx.mvx.core.model.WeatherModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 单向数据流（UDF）：状态向下 -- 通过 [uiState] 暴露；事件向上 -- 以方法调用传入（[query]）。
 * ViewModel 不持有 Context / View，旋转后保留，状态不丢。
 */
class WeatherViewModel(private val model: WeatherModel) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var job: Job? = null

    fun query(city: String, simulateFailure: Boolean = false) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = WeatherUiState(isLoading = true)
            _uiState.value = model.getWeather(city, simulateFailure).fold(
                onSuccess = { WeatherUiState(info = it) },
                onFailure = { WeatherUiState(isError = true) },
            )
        }
    }
}
