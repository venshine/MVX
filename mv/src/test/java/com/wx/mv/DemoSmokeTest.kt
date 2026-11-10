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
import com.wx.mv.ui.WeatherActivity
import com.wx.mvx.core.DemoTheme
import com.wx.mvx.core.R
import java.time.Duration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

/**
 * 示例页冒烟测试（Compose）：每个页面在浅色、深色下都能启动，关键交互的结果符合预期。
 * 用 testTag 查找节点，tag 与 View 版的 id 同名。
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
class DemoSmokeTest {

    @get:Rule
    val rule = createEmptyComposeRule()

    private val scenarios = mutableListOf<ActivityScenario<*>>()

    @After
    fun tearDown() {
        scenarios.forEach { it.close() }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    /** 模拟查询的 2 秒延迟走主线程 Looper，需要推进它的时钟 */
    private fun advance(millis: Long) {
        // 分段推进：每段之后让 Compose 有机会处理重组，避免延迟结束与重组错开
        var left = millis
        do {
            val step = minOf(left, 100L)
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(step))
            rule.mainClock.advanceTimeBy(step)
            rule.waitForIdle()
            left -= step
        } while (left > 0)
    }

    private fun <T : android.app.Activity> launch(type: Class<T>): ActivityScenario<T> {
        val scenario = ActivityScenario.launch(type)
        scenarios += scenario
        rule.waitForIdle()
        return scenario
    }

    private fun textOf(tag: String): String =
        rule.onNodeWithTag(tag).fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)
            ?.joinToString("") { it.text }.orEmpty()

    private fun tagText(tag: String, text: String) = assertEquals(text, textOf(tag))

    @Test
    fun allScreensLaunchInLightMode() {
        listOf(MainActivity::class.java, WeatherActivity::class.java).forEach { type ->
            launch(type).onActivity { assertTrue("${type.simpleName} 应为浅色", !DemoTheme.isNight(it)) }
        }
    }

    @Test
    fun allScreensLaunchInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        listOf(MainActivity::class.java, WeatherActivity::class.java).forEach { type ->
            launch(type).onActivity { assertTrue("${type.simpleName} 应为深色", DemoTheme.isNight(it)) }
        }
    }

    @Test
    fun themeToggleSwitchesToDark() {
        launch(MainActivity::class.java)
        rule.onNodeWithTag("theme_toggle").performClick()
        advance(0)
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.getDefaultNightMode())
    }

    @Test
    fun homeShowsResponsibilities() {
        val scenario = launch(MainActivity::class.java)
        lateinit var mid: String
        lateinit var flow: String
        scenario.onActivity {
            mid = it.getString(com.wx.mv.R.string.mv_role_mid)
            flow = it.getString(com.wx.mv.R.string.mv_flow)
        }
        tagText("role_mid_name", mid)
        tagText("flow_text", flow)
    }

    @Test
    fun homeCardOpensWeatherPage() {
        val scenario = launch(MainActivity::class.java)
        rule.onNodeWithTag("card_weather").performScrollTo().performClick()
        advance(0)
        scenario.onActivity {
            val next: Intent = shadowOf(it).nextStartedActivity
            assertEquals(WeatherActivity::class.java.name, next.component?.className)
        }
    }

    @Test
    fun weatherStartsIdle() {
        val scenario = launch(WeatherActivity::class.java)
        lateinit var idle: String
        scenario.onActivity { idle = it.getString(R.string.idle) }
        tagText("state_tag", "IDLE")
        tagText("status", idle)
        rule.onNodeWithTag("progress").assertDoesNotExist()
    }

    @Test
    fun weatherQueryGoesLoadingThenSuccess() {
        val scenario = launch(WeatherActivity::class.java)
        lateinit var city: String
        scenario.onActivity { city = it.getString(R.string.city_default) }
        rule.onNodeWithTag("query_button").performClick()
        advance(0)
        tagText("state_tag", "LOADING")
        rule.onNodeWithTag("progress").assertExists()
        advance(3000)
        tagText("state_tag", "SUCCESS")
        tagText("city", city)
    }

    @Test
    fun weatherSimulatedFailureShowsError() {
        val scenario = launch(WeatherActivity::class.java)
        lateinit var failure: String
        scenario.onActivity { failure = it.getString(R.string.failure) }
        rule.onNodeWithTag("fail_switch").performClick()
        rule.onNodeWithTag("query_button").performClick()
        advance(3000)
        tagText("state_tag", "ERROR")
        tagText("status", failure)
        tagText("temp", "")
    }

    @Test
    fun weatherEmptyCityShowsErrorInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        launch(WeatherActivity::class.java)
        rule.onNodeWithTag("city_edit").performTextClearance()
        rule.onNodeWithTag("query_button").performClick()
        advance(3000)
        tagText("state_tag", "ERROR")
    }

    @Test
    fun recreateKeepsAFinishedResult() {
        val scenario = launch(WeatherActivity::class.java)
        rule.onNodeWithTag("query_button").performClick()
        advance(3000)
        tagText("state_tag", "SUCCESS")
        scenario.recreate()
        rule.waitForIdle()
        // rememberSaveable 把结果存进 Bundle，重建后恢复
        tagText("state_tag", "SUCCESS")
    }

    @Test
    fun recreateWhileLoadingDropsTheQuery() {
        val scenario = launch(WeatherActivity::class.java)
        rule.onNodeWithTag("query_button").performClick()
        advance(500)
        tagText("state_tag", "LOADING")
        scenario.recreate()
        rule.waitForIdle()
        // 协程作用域随旧组合取消：进行中的查询不会保留，恢复为空闲，也不会自己完成
        tagText("state_tag", "IDLE")
        advance(3000)
        tagText("state_tag", "IDLE")
    }
}
