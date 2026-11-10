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
package com.wx.mvx

import android.app.Activity
import android.content.Intent
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.test.core.app.ActivityScenario
import com.wx.mvx.core.DemoTheme
import com.wx.mvx.core.R as CoreR
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import java.time.Duration

/**
 * app 冒烟测试（Compose 部分）：从首页进入 UDF 和 MV，完成查询成功 / 失败，浅色与深色。
 * 用 testTag 找节点，tag 与 View 版的 id 同名。
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
class ComposeFlowSmokeTest {

    @get:Rule
    val rule = createEmptyComposeRule()

    private val scenarios = mutableListOf<ActivityScenario<*>>()

    @After
    fun tearDown() {
        scenarios.forEach { it.close() }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    /** 模拟查询的 2 秒延迟走主线程 Looper，需要同时推进它和 Compose 的时钟 */
    private fun advance(millis: Long) {
        var left = millis
        do {
            val step = minOf(left, 100L)
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(step))
            rule.mainClock.advanceTimeBy(step)
            rule.waitForIdle()
            left -= step
        } while (left > 0)
    }

    private fun launch(intent: Intent): ActivityScenario<Activity> {
        val scenario = ActivityScenario.launch<Activity>(intent)
        scenarios += scenario
        rule.waitForIdle()
        return scenario
    }

    private fun textOf(tag: String): String =
        rule.onNodeWithTag(tag).fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)
            ?.joinToString("") { it.text }.orEmpty()

    private val composeArchitectures get() = architectures.filter { it.compose }

    /** 首页 -> 架构入口 -> 天气页，返回天气页的 Scenario */
    private fun openWeather(arch: Architecture): ActivityScenario<Activity> {
        val app = RuntimeEnvironment.getApplication()
        val hub = launch(Intent(app, MainActivity::class.java))
        lateinit var entryIntent: Intent
        hub.onActivity {
            it.findViewById<android.view.ViewGroup>(R.id.arch_list).findViewWithTag<android.view.View>(arch.key).performClick()
            entryIntent = shadowOf(it).nextStartedActivity
        }
        assertEquals(arch.entry.name, entryIntent.component?.className)
        hub.close()
        val entry = launch(entryIntent)
        rule.onNodeWithTag("card_weather").performScrollTo().performClick()
        advance(0)
        lateinit var weatherIntent: Intent
        entry.onActivity { weatherIntent = shadowOf(it).nextStartedActivity }
        // 关掉前面的页面，保证 Compose 树里只剩天气页，testTag 不会重复
        entry.close()
        return launch(weatherIntent)
    }

    @Test
    fun composeArchitecturesQuerySuccess() {
        composeArchitectures.forEach { arch ->
            val weather = openWeather(arch)
            lateinit var city: String
            weather.onActivity { city = it.getString(CoreR.string.city_default) }
            assertEquals("${arch.key} IDLE", "IDLE", textOf("state_tag"))
            rule.onNodeWithTag("query_button").performClick()
            advance(0)
            assertEquals("${arch.key} LOADING", "LOADING", textOf("state_tag"))
            advance(3000)
            assertEquals("${arch.key} SUCCESS", "SUCCESS", textOf("state_tag"))
            assertEquals(city, textOf("city"))
            weather.close()
        }
    }

    @Test
    fun composeArchitecturesSimulatedFailureShowsError() {
        composeArchitectures.forEach { arch ->
            val weather = openWeather(arch)
            lateinit var failure: String
            weather.onActivity { failure = it.getString(CoreR.string.failure) }
            rule.onNodeWithTag("fail_switch").performClick()
            rule.onNodeWithTag("query_button").performClick()
            advance(3000)
            assertEquals("${arch.key} ERROR", "ERROR", textOf("state_tag"))
            assertEquals(failure, textOf("status"))
            weather.close()
        }
    }

    @Test
    fun composeArchitecturesEmptyCityShowsErrorInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        composeArchitectures.forEach { arch ->
            val weather = openWeather(arch)
            weather.onActivity { assertTrue(DemoTheme.isNight(it)) }
            rule.onNodeWithTag("city_edit").performTextClearance()
            rule.onNodeWithTag("query_button").performClick()
            advance(3000)
            assertEquals("${arch.key} ERROR", "ERROR", textOf("state_tag"))
            weather.close()
        }
    }
}
