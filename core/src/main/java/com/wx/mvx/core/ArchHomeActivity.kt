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
package com.wx.mvx.core

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * View 架构模块的入口页：本架构的职责划分 + 天气查询入口。
 * 排版在 activity_arch_home.xml，文案由子类通过 [info] 提供。
 *
 * @author venshine
 */
abstract class ArchHomeActivity : BaseActivity() {

    protected abstract val info: ArchInfo

    /** 点击天气查询卡片时进入的页面 */
    protected abstract val weatherActivity: Class<out Activity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arch_home)
        findViewById<View>(R.id.source_button).setOnClickListener { openSource() }
        findViewById<TextView>(R.id.version_chip).text = versionTag()

        findViewById<TextView>(R.id.brand_name).setText(info.name)
        findViewById<TextView>(R.id.flow_text).setText(info.flow)
        findViewById<TextView>(R.id.home_title).setText(info.title)
        findViewById<TextView>(R.id.home_desc).setText(info.desc)
        findViewById<TextView>(R.id.role_view_name).setText(info.roleView)
        findViewById<TextView>(R.id.role_mid_name).setText(info.roleMid)
        findViewById<TextView>(R.id.role_model_name).setText(info.roleModel)
        findViewById<TextView>(R.id.role_view_desc).setText(info.roleViewDesc)
        findViewById<TextView>(R.id.role_mid_desc).setText(info.roleMidDesc)
        findViewById<TextView>(R.id.role_model_desc).setText(info.roleModelDesc)

        val card = findViewById<View>(R.id.card_weather)
        card.findViewById<ImageView>(R.id.card_icon).setImageResource(R.drawable.ic_cloud)
        card.findViewById<TextView>(R.id.card_title).setText(R.string.demo_weather)
        card.findViewById<TextView>(R.id.card_desc).setText(R.string.demo_weather_desc)
        card.setOnClickListener { startActivity(Intent(this, weatherActivity)) }
    }
}
