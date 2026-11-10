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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.wx.mvvm.view.WeatherActivity

/**
 * 首页：本架构的职责划分 + 天气查询入口
 *
 * @author venshine
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.source_button).setOnClickListener { openSource() }
        // 版本号只在本模块 build.gradle 的 versionName 维护一处
        val version = packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
        findViewById<TextView>(R.id.version_chip).text = "v$version"

        val card = findViewById<View>(R.id.card_weather)
        card.findViewById<ImageView>(R.id.card_icon).setImageResource(R.drawable.ic_cloud)
        card.findViewById<TextView>(R.id.card_title).setText(R.string.demo_weather)
        card.findViewById<TextView>(R.id.card_desc).setText(R.string.demo_weather_desc)
        card.setOnClickListener { startActivity(Intent(this, WeatherActivity::class.java)) }
    }

    private fun openSource() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, SOURCE_URL, Toast.LENGTH_LONG).show()
        }
    }

    private companion object {
        const val SOURCE_URL = "https://github.com/venshine/MVX"
    }
}
