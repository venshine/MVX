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
package com.wx.mv.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.wx.mvx.core.model.WeatherModel
import com.wx.mvx.core.model.entity.WeatherInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** 界面状态：互斥的四种 */
sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val info: WeatherInfo) : WeatherUiState
    data object Error : WeatherUiState
}

/**
 * MV：没有 ViewModel，状态放在这个普通类里，类的实例由可组合函数用 [rememberWeatherHolder] 持有。
 * 状态用 `mutableStateOf`，界面读它就会随变化重组；协程作用域来自组合（离开界面即取消）。
 *
 * 和 ViewModel 的区别：Activity 因旋转重建时，状态靠 rememberSaveable 存进 Bundle 恢复，
 * 但协程作用域随旧组合取消，**进行中的查询不会保留**；恢复时「加载中」退回「空闲」。
 */
class WeatherHolder(
    private val model: WeatherModel,
    private val scope: CoroutineScope,
    initial: WeatherUiState = WeatherUiState.Idle,
) {
    var state: WeatherUiState by mutableStateOf(initial)
        private set

    private var job: Job? = null

    fun query(city: String, simulateFailure: Boolean = false) {
        job?.cancel()
        job = scope.launch {
            state = WeatherUiState.Loading
            state = model.getWeather(city, simulateFailure).fold(
                onSuccess = { WeatherUiState.Success(it) },
                onFailure = { WeatherUiState.Error },
            )
        }
    }

    companion object {
        /** 存成纯字符串列表放进 Bundle：[tag, city, temp, weather]；加载中不存（见类注释） */
        fun saver(model: WeatherModel, scope: CoroutineScope): Saver<WeatherHolder, Any> = listSaver(
            save = { holder ->
                when (val s = holder.state) {
                    is WeatherUiState.Success -> listOf("success", s.info.city, s.info.temp, s.info.weather)
                    WeatherUiState.Error -> listOf("error")
                    else -> listOf("idle")
                }
            },
            restore = { saved ->
                val initial = when (saved[0]) {
                    "success" -> WeatherUiState.Success(WeatherInfo(saved[1], saved[2], saved[3]))
                    "error" -> WeatherUiState.Error
                    else -> WeatherUiState.Idle
                }
                WeatherHolder(model, scope, initial)
            },
        )
    }
}

/** 在组合里创建并记住 [WeatherHolder]，旋转重建时从 Bundle 恢复 */
@Composable
fun rememberWeatherHolder(model: WeatherModel): WeatherHolder {
    val scope = rememberCoroutineScope()
    return rememberSaveable(model, scope, saver = WeatherHolder.saver(model, scope)) {
        WeatherHolder(model, scope)
    }
}
