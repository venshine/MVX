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

import android.app.Activity
import com.wx.mvvm.view.WeatherActivity
import com.wx.mvx.core.ArchHomeActivity
import com.wx.mvx.core.ArchInfo

/**
 * MVVM 模块对外的唯一入口：本架构的职责划分 + 天气查询入口
 *
 * @author venshine
 */
class MainActivity : ArchHomeActivity() {

    override val info = ArchInfo(
        name = R.string.mvvm_name,
        flow = R.string.mvvm_flow,
        roleView = R.string.mvvm_role_view,
        roleMid = R.string.mvvm_role_mid,
        roleModel = R.string.mvvm_role_model,
        title = R.string.mvvm_title,
        desc = R.string.mvvm_desc,
        roleViewDesc = R.string.mvvm_role_view_desc,
        roleMidDesc = R.string.mvvm_role_mid_desc,
        roleModelDesc = R.string.mvvm_role_model_desc,
    )

    override val weatherActivity: Class<out Activity> = WeatherActivity::class.java
}
