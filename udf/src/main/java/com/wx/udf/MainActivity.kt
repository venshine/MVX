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
package com.wx.udf

import android.app.Activity
import com.wx.mvx.compose.ArchHomeActivity
import com.wx.mvx.core.ArchInfo
import com.wx.udf.ui.WeatherActivity

/**
 * UDF 模块对外的唯一入口：本架构的职责划分 + 天气查询入口
 *
 * @author venshine
 */
class MainActivity : ArchHomeActivity() {

    override val info = ArchInfo(
        name = R.string.udf_name,
        flow = R.string.udf_flow,
        roleView = R.string.udf_role_view,
        roleMid = R.string.udf_role_mid,
        roleModel = R.string.udf_role_model,
        title = R.string.udf_title,
        desc = R.string.udf_desc,
        roleViewDesc = R.string.udf_role_view_desc,
        roleMidDesc = R.string.udf_role_mid_desc,
        roleModelDesc = R.string.udf_role_model_desc,
    )

    override val weatherActivity: Class<out Activity> = WeatherActivity::class.java
}
