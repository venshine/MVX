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
package com.wx.mvi

import com.wx.mvi.mvi.Status
import com.wx.mvi.mvi.WeatherEffect
import com.wx.mvi.mvi.WeatherIntent
import com.wx.mvi.mvi.WeatherState
import com.wx.mvi.mvi.WeatherViewModel
import com.wx.mvx.core.model.WeatherModel
import com.wx.mvx.core.model.entity.WeatherInfo
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

/** 纯 JVM 测试：Intent 进、状态序列和 Effect 出 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val info = WeatherInfo("北京", "18度-25度", "多云转晴")

    private class FakeModel(val result: Result<WeatherInfo>) : WeatherModel {
        override suspend fun getWeather(city: String, simulateFailure: Boolean): Result<WeatherInfo> {
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
    fun success_idleLoadingSuccess_noEffect() = runTest {
        val vm = WeatherViewModel(FakeModel(Result.success(info)))
        val states = mutableListOf<WeatherState>()
        val effects = mutableListOf<WeatherEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.toList(states) }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.effects.toList(effects) }
        vm.dispatch(WeatherIntent.Query("北京", false))
        advanceUntilIdle()
        assertEquals(
            listOf(WeatherState(), WeatherState(Status.LOADING), WeatherState(Status.SUCCESS, info)),
            states,
        )
        assertEquals(emptyList<WeatherEffect>(), effects)
    }

    @Test
    fun failure_idleLoadingError_andOneShowFailureEffect() = runTest {
        val vm = WeatherViewModel(FakeModel(Result.failure(IllegalArgumentException())))
        val states = mutableListOf<WeatherState>()
        val effects = mutableListOf<WeatherEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.state.toList(states) }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.effects.toList(effects) }
        vm.dispatch(WeatherIntent.Query("", false))
        advanceUntilIdle()
        assertEquals(
            listOf(WeatherState(), WeatherState(Status.LOADING), WeatherState(Status.ERROR)),
            states,
        )
        assertEquals(listOf<WeatherEffect>(WeatherEffect.ShowFailure), effects)
    }

    @Test
    fun effectIsBufferedUntilConsumedAndOnlyOnce() = runTest {
        val vm = WeatherViewModel(FakeModel(Result.failure(IllegalStateException())))
        vm.dispatch(WeatherIntent.Query("北京", true))
        advanceUntilIdle()
        // 还没人收集时事件先缓存；第一个收集者拿到，第二个收集者不会再拿到（不重放）
        val first = mutableListOf<WeatherEffect>()
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.effects.toList(first) }
        advanceUntilIdle()
        job.cancel()
        val second = mutableListOf<WeatherEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.effects.toList(second) }
        advanceUntilIdle()
        assertEquals(listOf<WeatherEffect>(WeatherEffect.ShowFailure), first)
        assertEquals(emptyList<WeatherEffect>(), second)
    }

    @Test
    fun newQueryCancelsThePreviousOne() = runTest {
        val vm = WeatherViewModel(FakeModel(Result.success(info)))
        vm.dispatch(WeatherIntent.Query("北京", false))
        advanceTimeBy(600)
        vm.dispatch(WeatherIntent.Query("上海", false))
        advanceTimeBy(600)
        // 第一次查询的 1000ms 已过，但它已被取消，状态仍是第二次的加载中
        assertEquals(Status.LOADING, vm.state.value.status)
        advanceUntilIdle()
        assertEquals(Status.SUCCESS, vm.state.value.status)
    }
}
