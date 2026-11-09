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
package com.wx.mvp.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wx.mvp.R
import com.wx.mvp.model.entity.WeatherInfo
import com.wx.mvp.model.impl.WeatherModelImpl
import com.wx.mvp.presenter.WeatherPresenter
import kotlinx.coroutines.Dispatchers

/**
 * MVP 的 View：只负责展示和转发点击，业务逻辑都在 [WeatherPresenter]。
 *
 * @author venshine
 */
class WeatherActivity : AppCompatActivity(), WeatherView {

    private val presenter = WeatherPresenter(WeatherModelImpl(), Dispatchers.Main.immediate)

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
        findViewById<Button>(R.id.query_button).setOnClickListener {
            presenter.getWeather(cityEdit.text.toString())
        }
        presenter.attach(this)
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    override fun showLoading() {
        progress.visibility = View.VISIBLE
        status.setText(R.string.loading)
        city.text = ""
        temp.text = ""
        weather.text = ""
    }

    override fun showWeather(info: WeatherInfo) {
        progress.visibility = View.GONE
        status.setText(R.string.success)
        city.text = info.city
        temp.text = info.temp
        weather.text = info.weather
    }

    override fun showError() {
        progress.visibility = View.GONE
        status.setText(R.string.failure)
    }
}
