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
package com.wx.mvx.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import com.wx.mvx.core.DemoTheme
import com.wx.mvx.core.R

/**
 * Compose 页面的公共部分：全面屏、状态栏图标颜色，并把界面内容交给 Compose。
 * 继承 AppCompatActivity 只是为了沿用 [DemoTheme] 的深浅色切换（AppCompatDelegate），界面本身不用 View。
 *
 * @author venshine
 */
abstract class BaseActivity : AppCompatActivity() {

    @Composable
    protected abstract fun Content()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // targetSdk 35 起，Android 15+ 强制全面屏，内容自己避开系统栏（见 DemoPage 的 WindowInsets）
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val light = !DemoTheme.isNight(this)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = light
            isAppearanceLightNavigationBars = light
        }
        setContent { Content() }
    }
}
