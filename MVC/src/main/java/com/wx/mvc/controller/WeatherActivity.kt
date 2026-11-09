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
package com.wx.mvc.controller

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.wx.mvc.R
import com.wx.mvc.model.WeatherModel
import com.wx.mvc.model.entity.WeatherInfo
import com.wx.mvc.model.impl.WeatherModelImpl
import kotlinx.coroutines.launch

/**
 * MVC：Activity 兼任 Controller。
 * 读取输入、调用 Model、直接操作 View 展示结果，三件事都在这里。
 * 协程挂在 lifecycleScope 上，Activity 销毁时自动取消。
 *
 * @author venshine
 */
class WeatherActivity : AppCompatActivity() {

    private val model: WeatherModel = WeatherModelImpl()

    private lateinit var cityEdit: EditText
    private lateinit var progress: ProgressBar
    private lateinit var status: TextView
    private lateinit var city: TextView
    private lateinit var temp: TextView
    private lateinit var weather: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_weather)
        val root = findViewById<View>(R.id.root)
        // targetSdk 35+ 强制 edge-to-edge，给根布局补上左右和底部的系统栏边距
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, bars.bottom)
            insets
        }
        cityEdit = findViewById(R.id.city_edit)
        progress = findViewById(R.id.progress)
        status = findViewById(R.id.status)
        city = findViewById(R.id.city)
        temp = findViewById(R.id.temp)
        weather = findViewById(R.id.weather)
        findViewById<Button>(R.id.query_button).setOnClickListener { query() }
    }

    private fun query() {
        showLoading()
        lifecycleScope.launch {
            model.getWeather(cityEdit.text.toString()).fold(::showWeather) { showError() }
        }
    }

    private fun showLoading() {
        progress.visibility = View.VISIBLE
        status.setText(R.string.loading)
        city.text = ""
        temp.text = ""
        weather.text = ""
    }

    private fun showWeather(info: WeatherInfo) {
        progress.visibility = View.GONE
        status.setText(R.string.success)
        city.text = info.city
        temp.text = info.temp
        weather.text = info.weather
    }

    private fun showError() {
        progress.visibility = View.GONE
        status.setText(R.string.failure)
    }
}
