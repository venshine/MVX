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
package com.wx.udf.ui

import androidx.compose.runtime.Composable
import com.wx.mvx.compose.BaseActivity
import com.wx.mvx.core.DemoTheme

/**
 * 天气页：只负责把 Compose 内容挂上去，业务都在 ViewModel
 *
 * @author venshine
 */
internal class WeatherActivity : BaseActivity() {

    @Composable
    override fun Content() {
        WeatherScreen(onBack = ::finish, onToggleTheme = { DemoTheme.toggle(this) })
    }
}
