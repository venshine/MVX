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

import android.app.Activity
import android.content.Intent
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.wx.mvp.view.WeatherActivity
import com.wx.mvx.core.DemoTheme
import com.wx.mvx.core.R
import java.time.Duration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController

/**
 * 示例页冒烟测试：每个页面在浅色、深色下都能启动，关键交互的结果符合预期
 *
 * @author venshine
 */
@RunWith(RobolectricTestRunner::class)
class DemoSmokeTest {

    private val launched = mutableListOf<ActivityController<*>>()

    @After
    fun tearDown() {
        // 销毁本用例启动过的 Activity，避免残留协程和窗口影响后续用例
        launched.forEach { it.pause().stop().destroy() }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun idle(seconds: Long = 1) {
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(seconds))
    }

    private fun <T : Activity> launch(type: Class<T>): T {
        val controller = Robolectric.buildActivity(type).setup()
        launched += controller
        idle()
        return controller.get()
    }

    private val screens = listOf(MainActivity::class.java, WeatherActivity::class.java)

    @Test
    fun allScreensLaunchInLightMode() {
        screens.forEach { type ->
            val activity = launch(type)
            assertTrue("${type.simpleName} 应为浅色", !DemoTheme.isNight(activity))
        }
    }

    @Test
    fun allScreensLaunchInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        screens.forEach { type ->
            val activity = launch(type)
            assertTrue("${type.simpleName} 应为深色", DemoTheme.isNight(activity))
        }
    }

    @Test
    fun themeToggleSwitchesToDark() {
        val activity = launch(MainActivity::class.java)
        activity.findViewById<View>(R.id.theme_toggle).performClick()
        idle()
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.getDefaultNightMode())
    }

    @Test
    fun homeShowsResponsibilities() {
        val activity = launch(MainActivity::class.java)
        assertEquals(activity.getString(com.wx.mvp.R.string.mvp_role_mid), activity.findViewById<TextView>(R.id.role_mid_name).text.toString())
        assertEquals(activity.getString(com.wx.mvp.R.string.mvp_flow), activity.findViewById<TextView>(R.id.flow_text).text.toString())
    }

    @Test
    fun homeCardOpensWeatherPage() {
        val activity = launch(MainActivity::class.java)
        activity.findViewById<View>(R.id.card_weather).performClick()
        val next: Intent = shadowOf(activity).nextStartedActivity
        assertEquals(WeatherActivity::class.java.name, next.component?.className)
    }

    @Test
    fun weatherStartsIdle() {
        val activity = launch(WeatherActivity::class.java)
        assertEquals("IDLE", activity.findViewById<TextView>(R.id.state_tag).text.toString())
        assertEquals(activity.getString(R.string.idle), activity.findViewById<TextView>(R.id.status).text.toString())
        assertEquals(View.GONE, activity.findViewById<View>(R.id.progress).visibility)
    }

    @Test
    fun weatherQueryGoesLoadingThenSuccess() {
        val activity = launch(WeatherActivity::class.java)
        activity.findViewById<View>(R.id.query_button).performClick()
        idle(0)
        assertEquals("LOADING", activity.findViewById<TextView>(R.id.state_tag).text.toString())
        assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.progress).visibility)
        idle(3)
        assertEquals("SUCCESS", activity.findViewById<TextView>(R.id.state_tag).text.toString())
        assertEquals(activity.getString(R.string.city_default), activity.findViewById<TextView>(R.id.city).text.toString())
    }

    @Test
    fun weatherSimulatedFailureShowsError() {
        val activity = launch(WeatherActivity::class.java)
        activity.findViewById<SwitchCompat>(R.id.fail_switch).isChecked = true
        activity.findViewById<View>(R.id.query_button).performClick()
        idle(3)
        assertEquals("ERROR", activity.findViewById<TextView>(R.id.state_tag).text.toString())
        assertEquals(activity.getString(R.string.failure), activity.findViewById<TextView>(R.id.status).text.toString())
        assertEquals("", activity.findViewById<TextView>(R.id.temp).text.toString())
    }

    @Test
    fun weatherEmptyCityShowsErrorInDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val activity = launch(WeatherActivity::class.java)
        activity.findViewById<EditText>(R.id.city_edit).setText("")
        activity.findViewById<View>(R.id.query_button).performClick()
        idle(3)
        assertEquals("ERROR", activity.findViewById<TextView>(R.id.state_tag).text.toString())
    }
}
