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
package com.wx.udf

import com.wx.mvx.core.model.WeatherModel
import com.wx.mvx.core.model.entity.WeatherInfo
import com.wx.udf.viewmodel.WeatherUiState
import com.wx.udf.viewmodel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** 纯 JVM 测试：用 [StandardTestDispatcher] 当 Main，假 Model 控制成功 / 失败 / 延迟，断言 uiState 的序列 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val info = WeatherInfo("北京", "18度-25度", "多云转晴")

    private class FakeModel(val result: Result<WeatherInfo>) : WeatherModel {
        var lastSimulateFailure: Boolean? = null
        override suspend fun getWeather(city: String, simulateFailure: Boolean): Result<WeatherInfo> {
            lastSimulateFailure = simulateFailure
            delay(1000)
            return result
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun success_idleLoadingSuccess() = runTest {
        val vm = WeatherViewModel(FakeModel(Result.success(info)))
        val states = mutableListOf<WeatherUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.toList(states) }
        vm.query("北京")
        advanceUntilIdle()
        assertEquals(
            listOf(WeatherUiState(), WeatherUiState(isLoading = true), WeatherUiState(info = info)),
            states,
        )
    }

    @Test
    fun failure_idleLoadingError() = runTest {
        val vm = WeatherViewModel(FakeModel(Result.failure(IllegalArgumentException())))
        val states = mutableListOf<WeatherUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.toList(states) }
        vm.query("")
        advanceUntilIdle()
        assertEquals(
            listOf(WeatherUiState(), WeatherUiState(isLoading = true), WeatherUiState(isError = true)),
            states,
        )
    }

    @Test
    fun stillLoadingBeforeModelReturns() = runTest {
        val vm = WeatherViewModel(FakeModel(Result.success(info)))
        vm.query("北京")
        advanceTimeBy(500)
        assertEquals(WeatherUiState(isLoading = true), vm.uiState.value)
    }

    @Test
    fun newQueryReplacesTheOldOneAndPassesSimulateFailure() = runTest {
        val model = FakeModel(Result.success(info))
        val vm = WeatherViewModel(model)
        vm.query("北京")
        advanceTimeBy(600)
        vm.query("上海", simulateFailure = true)
        advanceTimeBy(600)
        // 第一次的 1000ms 已过但被取消，仍在第二次的加载中
        assertEquals(WeatherUiState(isLoading = true), vm.uiState.value)
        assertEquals(true, model.lastSimulateFailure)
        advanceUntilIdle()
        assertEquals(WeatherUiState(info = info), vm.uiState.value)
    }
}
