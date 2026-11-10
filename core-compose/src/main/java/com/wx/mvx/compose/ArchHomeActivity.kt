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

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import com.wx.mvx.core.ArchInfo
import com.wx.mvx.core.DemoTheme
import com.wx.mvx.core.openSource
import com.wx.mvx.core.versionTag

/**
 * Compose 架构模块的入口页：本架构的职责划分 + 天气查询入口。
 * 排版在 [HomeScreen]，文案由子类通过 [info] 提供。
 *
 * @author venshine
 */
abstract class ArchHomeActivity : BaseActivity() {

    protected abstract val info: ArchInfo

    /** 点击天气查询卡片时进入的页面 */
    protected abstract val weatherActivity: Class<out Activity>

    @Composable
    override fun Content() {
        HomeScreen(
            info = info,
            version = versionTag(),
            onToggleTheme = { DemoTheme.toggle(this) },
            onSource = { openSource() },
            onOpenWeather = { startActivity(Intent(this, weatherActivity)) },
        )
    }
}
