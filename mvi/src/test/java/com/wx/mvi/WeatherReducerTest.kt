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
import com.wx.mvi.mvi.WeatherChange
import com.wx.mvi.mvi.WeatherReducer
import com.wx.mvi.mvi.WeatherState
import com.wx.mvx.core.model.entity.WeatherInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

/** Reducer 是纯函数：不需要协程、不需要 Android，直接断言「旧状态 + 变化 = 新状态」 */
class WeatherReducerTest {

    private val info = WeatherInfo("北京", "18度-25度", "多云转晴")

    @Test
    fun loadingClearsPreviousResult() {
        val before = WeatherState(Status.SUCCESS, info)
        assertEquals(WeatherState(Status.LOADING, null), WeatherReducer.reduce(before, WeatherChange.Loading))
    }

    @Test
    fun loadedCarriesInfo() {
        val after = WeatherReducer.reduce(WeatherState(Status.LOADING), WeatherChange.Loaded(info))
        assertEquals(WeatherState(Status.SUCCESS, info), after)
    }

    @Test
    fun failedClearsInfo() {
        val after = WeatherReducer.reduce(WeatherState(Status.LOADING), WeatherChange.Failed)
        assertEquals(WeatherState(Status.ERROR, null), after)
    }

    @Test
    fun sameInputGivesSameOutputAndNeverMutatesOldState() {
        val before = WeatherState(Status.LOADING)
        val first = WeatherReducer.reduce(before, WeatherChange.Loaded(info))
        val second = WeatherReducer.reduce(before, WeatherChange.Loaded(info))
        assertEquals(first, second)
        assertNotSame(before, first)
        assertEquals(WeatherState(Status.LOADING), before)
    }

    @Test
    fun foldingAChangeSequenceEndsInTheLastState() {
        val changes = listOf(WeatherChange.Loading, WeatherChange.Failed, WeatherChange.Loading, WeatherChange.Loaded(info))
        val end = changes.fold(WeatherState()) { s, c -> WeatherReducer.reduce(s, c) }
        assertEquals(WeatherState(Status.SUCCESS, info), end)
        assertSame(Status.SUCCESS, end.status)
    }
}
