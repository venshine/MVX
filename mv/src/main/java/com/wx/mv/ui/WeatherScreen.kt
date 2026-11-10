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
package com.wx.mv.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.wx.mv.state.WeatherUiState
import com.wx.mv.state.rememberWeatherHolder
import com.wx.mvx.compose.Phase
import com.wx.mvx.compose.WeatherContent
import com.wx.mvx.core.model.impl.WeatherModelImpl

/**
 * MV 的屏幕：自己持有状态持有者，读它的状态渲染，点击直接调它的方法。
 * 没有 ViewModel、没有 collect：Compose 的快照状态读取就是订阅。
 */
@Composable
fun WeatherScreen(onBack: () -> Unit, onToggleTheme: () -> Unit) {
    val model = remember { WeatherModelImpl() }
    val holder = rememberWeatherHolder(model)
    val state = holder.state
    WeatherContent(
        phase = when (state) {
            WeatherUiState.Idle -> Phase.IDLE
            WeatherUiState.Loading -> Phase.LOADING
            is WeatherUiState.Success -> Phase.SUCCESS
            WeatherUiState.Error -> Phase.ERROR
        },
        info = (state as? WeatherUiState.Success)?.info,
        chip = "MV",
        onQuery = holder::query,
        onBack = onBack,
        onToggleTheme = onToggleTheme,
    )
}
