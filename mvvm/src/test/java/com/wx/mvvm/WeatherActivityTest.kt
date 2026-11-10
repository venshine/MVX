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
package com.wx.mvvm

import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.wx.mvvm.view.WeatherActivity
import com.wx.mvx.core.R
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

/** 冒烟测试：启动、成功、失败 */
@RunWith(RobolectricTestRunner::class)
class WeatherActivityTest {

    private fun query(city: String): WeatherActivity {
        val activity = Robolectric.buildActivity(WeatherActivity::class.java).setup().get()
        activity.findViewById<EditText>(R.id.city_edit).setText(city)
        activity.findViewById<View>(R.id.query_button).performClick()
        shadowOf(Looper.getMainLooper()).idle()
        // 加载中
        assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.progress).visibility)
        assertEquals(activity.getString(R.string.loading), activity.findViewById<TextView>(R.id.status).text)
        shadowOf(Looper.getMainLooper()).idleFor(3, TimeUnit.SECONDS)
        return activity
    }

    @Test
    fun success() {
        val a = query("北京")
        assertEquals(View.GONE, a.findViewById<View>(R.id.progress).visibility)
        assertEquals(a.getString(R.string.success), a.findViewById<TextView>(R.id.status).text)
        assertEquals("北京", a.findViewById<TextView>(R.id.city).text)
        assertEquals("多云转晴", a.findViewById<TextView>(R.id.weather).text)
    }

    @Test
    fun failureWhenCityEmpty() {
        val a = query("")
        assertEquals(a.getString(R.string.failure), a.findViewById<TextView>(R.id.status).text)
        assertEquals("", a.findViewById<TextView>(R.id.city).text)
    }
}
