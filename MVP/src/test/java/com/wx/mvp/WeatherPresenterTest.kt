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
package com.wx.mvp

import com.wx.mvp.model.WeatherModel
import com.wx.mvp.model.entity.WeatherInfo
import com.wx.mvp.presenter.WeatherPresenter
import com.wx.mvp.view.WeatherView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** 纯 JVM 测试：Presenter 不依赖 Android，View 和 Model 都用假实现 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherPresenterTest {

    private val info = WeatherInfo("北京", "18度-25度", "多云转晴")

    private class RecordingView : WeatherView {
        val events = mutableListOf<String>()
        override fun showLoading() { events += "loading" }
        override fun showWeather(info: WeatherInfo) { events += "weather:" + info.city }
        override fun showError() { events += "error" }
    }

    private class FakeModel(val result: Result<WeatherInfo>, val delayMillis: Long = 1000) : WeatherModel {
        override suspend fun getWeather(city: String): Result<WeatherInfo> {
            delay(delayMillis)
            return result
        }
    }

    private fun TestScope.presenter(model: WeatherModel) =
        WeatherPresenter(model, StandardTestDispatcher(testScheduler))

    @Test
    fun success_emitsLoadingThenWeather() = runTest {
        val view = RecordingView()
        val p = presenter(FakeModel(Result.success(info))).also { it.attach(view) }
        p.getWeather("北京")
        assertEquals(listOf("loading"), view.events)
        advanceUntilIdle()
        assertEquals(listOf("loading", "weather:北京"), view.events)
    }

    @Test
    fun failure_emitsLoadingThenError() = runTest {
        val view = RecordingView()
        val p = presenter(FakeModel(Result.failure(IllegalArgumentException()))).also { it.attach(view) }
        p.getWeather("")
        advanceUntilIdle()
        assertEquals(listOf("loading", "error"), view.events)
    }

    @Test
    fun detach_cancelsPendingRequestAndReleasesView() = runTest {
        val view = RecordingView()
        val p = presenter(FakeModel(Result.success(info))).also { it.attach(view) }
        p.getWeather("北京")
        advanceTimeBy(500)
        p.detach()
        advanceUntilIdle()
        assertEquals(listOf("loading"), view.events)
    }

    @Test
    fun newQueryCancelsPreviousOne() = runTest {
        val view = RecordingView()
        val p = presenter(FakeModel(Result.success(info))).also { it.attach(view) }
        p.getWeather("北京")
        advanceTimeBy(500)
        p.getWeather("上海")
        advanceUntilIdle()
        assertEquals(listOf("loading", "loading", "weather:北京"), view.events)
    }
}
