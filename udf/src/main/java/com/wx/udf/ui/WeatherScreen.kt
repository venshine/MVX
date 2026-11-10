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
package com.wx.udf.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wx.mvx.compose.Phase
import com.wx.mvx.compose.WeatherContent
import com.wx.mvx.core.model.impl.WeatherModelImpl
import com.wx.udf.viewmodel.WeatherUiState
import com.wx.udf.viewmodel.WeatherViewModel

/** 状态阶段只用于界面配色，所以映射放在 UI 层，不进 ViewModel */
private fun WeatherUiState.toPhase() = when {
    isLoading -> Phase.LOADING
    isError -> Phase.ERROR
    info != null -> Phase.SUCCESS
    else -> Phase.IDLE
}

/**
 * 屏幕级入口：拿 ViewModel，用 collectAsStateWithLifecycle 订阅 uiState（前台才收集），
 * 再把状态和事件回调交给无状态的 [WeatherContent]。
 */
@Composable
fun WeatherScreen(
    onBack: () -> Unit,
    onToggleTheme: () -> Unit,
    viewModel: WeatherViewModel = viewModel(
        factory = viewModelFactory { initializer { WeatherViewModel(WeatherModelImpl()) } },
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WeatherContent(
        phase = uiState.toPhase(),
        info = uiState.info,
        chip = "UDF",
        onQuery = viewModel::query,
        onBack = onBack,
        onToggleTheme = onToggleTheme,
    )
}
