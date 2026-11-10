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
package com.wx.mv

import androidx.compose.runtime.saveable.SaverScope
import com.wx.mv.state.WeatherHolder
import com.wx.mv.state.WeatherUiState
import com.wx.mvx.core.model.WeatherModel
import com.wx.mvx.core.model.entity.WeatherInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** 纯 JVM 测试：状态持有者不依赖 Android，传入测试协程作用域即可驱动 */
class WeatherHolderTest {

    private val info = WeatherInfo("北京", "18度-25度", "多云转晴")

    private class FakeModel(val result: Result<WeatherInfo>) : WeatherModel {
        var lastSimulateFailure: Boolean? = null
        override suspend fun getWeather(city: String, simulateFailure: Boolean): Result<WeatherInfo> {
            lastSimulateFailure = simulateFailure
            delay(1000)
            return result
        }
    }

    private val allowAll = SaverScope { true }

    @Test
    fun startsIdle() = runTest {
        assertEquals(WeatherUiState.Idle, WeatherHolder(FakeModel(Result.success(info)), this).state)
    }

    @Test
    fun success_loadingThenSuccess() = runTest {
        val holder = WeatherHolder(FakeModel(Result.success(info)), this)
        holder.query("北京")
        advanceTimeBy(500)
        assertEquals(WeatherUiState.Loading, holder.state)
        advanceUntilIdle()
        assertEquals(WeatherUiState.Success(info), holder.state)
    }

    @Test
    fun failure_loadingThenError() = runTest {
        val holder = WeatherHolder(FakeModel(Result.failure(IllegalArgumentException())), this)
        holder.query("")
        advanceTimeBy(500)
        assertEquals(WeatherUiState.Loading, holder.state)
        advanceUntilIdle()
        assertEquals(WeatherUiState.Error, holder.state)
    }

    @Test
    fun newQueryReplacesTheOldOneAndPassesSimulateFailure() = runTest {
        val model = FakeModel(Result.success(info))
        val holder = WeatherHolder(model, this)
        holder.query("北京")
        advanceTimeBy(600)
        holder.query("上海", simulateFailure = true)
        advanceTimeBy(600)
        assertEquals(WeatherUiState.Loading, holder.state)
        assertEquals(true, model.lastSimulateFailure)
        advanceUntilIdle()
        assertEquals(WeatherUiState.Success(info), holder.state)
    }

    @Test
    fun saverKeepsResultsButNotAQueryInFlight() = runTest {
        val model = FakeModel(Result.success(info))
        val saver = WeatherHolder.saver(model, this)

        val done = WeatherHolder(model, this)
        done.query("北京")
        advanceUntilIdle()
        val restoredDone = saver.restore(with(saver) { allowAll.save(done)!! })!!
        assertEquals(WeatherUiState.Success(info), restoredDone.state)

        val failed = WeatherHolder(FakeModel(Result.failure(IllegalStateException())), this)
        failed.query("北京")
        advanceUntilIdle()
        assertEquals(WeatherUiState.Error, saver.restore(with(saver) { allowAll.save(failed)!! })!!.state)

        // 进行中的查询无法跨重建续上：恢复后回到空闲
        val loading = WeatherHolder(model, this)
        loading.query("北京")
        advanceTimeBy(500)
        assertEquals(WeatherUiState.Loading, loading.state)
        assertEquals(WeatherUiState.Idle, saver.restore(with(saver) { allowAll.save(loading)!! })!!.state)
        advanceUntilIdle()
    }
}
