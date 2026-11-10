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
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.wx.mvx.core.DemoTheme
import com.wx.mvx.core.R as CoreR
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import java.time.Duration

/**
 * app 冒烟测试（View 部分）：首页、对比页，以及从首页进入四个 View 架构完成查询的整条链路。
 * Compose 架构见 [ComposeFlowSmokeTest]。
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
class HubSmokeTest {

    private val launched = mutableListOf<ActivityController<*>>()

    @After
    fun tearDown() {
        launched.forEach { it.pause().stop().destroy() }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun idle(seconds: Long = 1) {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(seconds))
    }

    private fun launch(intent: Intent): Activity {
        @Suppress("UNCHECKED_CAST")
        val type = Class.forName(intent.component!!.className) as Class<Activity>
        val controller = Robolectric.buildActivity(type, intent).setup()
        launched += controller
        idle()
        return controller.get()
    }

    private fun launch(type: Class<out Activity>): Activity = launch(Intent(org.robolectric.RuntimeEnvironment.getApplication(), type))

    private fun Activity.text(id: Int) = findViewById<TextView>(id).text.toString()

    private fun Activity.card(key: String): View = findViewById<ViewGroup>(R.id.arch_list).findViewWithTag(key)

    @Test
    fun hubLaunchesInLightAndDarkMode() {
        assertTrue(!DemoTheme.isNight(launch(MainActivity::class.java)))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        assertTrue(DemoTheme.isNight(launch(MainActivity::class.java)))
    }

    @Test
    fun hubShowsVersionAndSixCards() {
        val hub = launch(MainActivity::class.java)
        val version = hub.text(R.id.version_chip)
        assertTrue("版本标签应为 v<versionName>：$version", Regex("v\\d+\\.\\d+\\.\\d+").matches(version))
        assertEquals(6, hub.findViewById<ViewGroup>(R.id.arch_list).childCount)
        architectures.forEach { arch ->
            assertNotNull("缺少 ${arch.key} 卡片", hub.card(arch.key))
            assertEquals(hub.getString(arch.name), hub.card(arch.key).findViewById<TextView>(R.id.card_title).text.toString())
            val tag = hub.card(arch.key).findViewById<TextView>(R.id.card_tag).text.toString()
            assertEquals(if (arch.compose) "COMPOSE" else "VIEW", tag)
        }
    }

    @Test
    fun themeToggleSwitchesToDark() {
        val hub = launch(MainActivity::class.java)
        hub.findViewById<View>(R.id.theme_toggle).performClick()
        idle()
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.getDefaultNightMode())
    }

    @Test
    fun everyCardOpensItsModuleEntry() {
        architectures.forEach { arch ->
            val hub = launch(MainActivity::class.java)
            hub.card(arch.key).performClick()
            val next = shadowOf(hub).nextStartedActivity
            assertEquals(arch.entry.name, next.component?.className)
        }
    }

    @Test
    fun compareCardOpensComparePage() {
        val hub = launch(MainActivity::class.java)
        hub.findViewById<View>(R.id.card_compare).performClick()
        assertEquals(CompareActivity::class.java.name, shadowOf(hub).nextStartedActivity.component?.className)
    }

    @Test
    fun compareListsFiveRowsForEachArchitecture() {
        val page = launch(CompareActivity::class.java)
        val list = page.findViewById<ViewGroup>(R.id.compare_list)
        assertEquals(6, list.childCount)
        architectures.forEach { arch ->
            val card = list.findViewWithTag<View>(arch.key)
            assertEquals(page.getString(arch.name), card.findViewById<TextView>(R.id.cmp_name).text.toString())
            val rows = card.findViewById<ViewGroup>(R.id.cmp_rows)
            assertEquals(5, rows.childCount)
            assertEquals(page.getString(arch.flow), rows.getChildAt(1).findViewById<TextView>(R.id.row_value).text.toString())
        }
    }

    private fun openWeather(arch: Architecture): Activity {
        val hub = launch(MainActivity::class.java)
        hub.card(arch.key).performClick()
        val entry = launch(shadowOf(hub).nextStartedActivity)
        entry.findViewById<View>(CoreR.id.card_weather).performClick()
        return launch(shadowOf(entry).nextStartedActivity)
    }

    private val viewArchitectures get() = architectures.filter { !it.compose }

    @Test
    fun viewArchitecturesQuerySuccess() {
        viewArchitectures.forEach { arch ->
            val weather = openWeather(arch)
            assertEquals("${arch.key} 应从 IDLE 开始", "IDLE", weather.text(CoreR.id.state_tag))
            weather.findViewById<View>(CoreR.id.query_button).performClick()
            idle(0)
            assertEquals("${arch.key} LOADING", "LOADING", weather.text(CoreR.id.state_tag))
            idle(3)
            assertEquals("${arch.key} SUCCESS", "SUCCESS", weather.text(CoreR.id.state_tag))
            assertEquals(weather.getString(CoreR.string.city_default), weather.text(CoreR.id.city))
        }
    }

    @Test
    fun viewArchitecturesSimulatedFailureShowsError() {
        viewArchitectures.forEach { arch ->
            val weather = openWeather(arch)
            weather.findViewById<SwitchCompat>(CoreR.id.fail_switch).isChecked = true
            weather.findViewById<View>(CoreR.id.query_button).performClick()
            idle(3)
            assertEquals("${arch.key} ERROR", "ERROR", weather.text(CoreR.id.state_tag))
            assertEquals(weather.getString(CoreR.string.failure), weather.text(CoreR.id.status))
        }
    }

    @Test
    fun viewArchitecturesEmptyCityShowsErrorInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        viewArchitectures.forEach { arch ->
            val weather = openWeather(arch)
            assertTrue(DemoTheme.isNight(weather))
            weather.findViewById<EditText>(CoreR.id.city_edit).setText("")
            weather.findViewById<View>(CoreR.id.query_button).performClick()
            idle(3)
            assertEquals("${arch.key} ERROR", "ERROR", weather.text(CoreR.id.state_tag))
        }
    }
}
