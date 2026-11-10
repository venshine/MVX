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
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.wx.mvc.BaseActivity
import com.wx.mvc.Phase
import com.wx.mvc.R
import com.wx.mvc.applyPhase
import com.wx.mvc.model.WeatherModel
import com.wx.mvc.model.entity.WeatherInfo
import com.wx.mvc.model.impl.WeatherModelImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * MVC：Activity 兼任 Controller。
 * 读取输入、调用 Model、直接操作 View 展示结果，三件事都在这里。
 * 协程挂在 lifecycleScope 上，Activity 销毁时自动取消。
 *
 * @author venshine
 */
class WeatherActivity : BaseActivity() {

    private val model: WeatherModel = WeatherModelImpl()
    private var job: Job? = null

    private lateinit var cityEdit: EditText
    private lateinit var failSwitch: SwitchCompat
    private lateinit var progress: ProgressBar
    private lateinit var statusCard: View
    private lateinit var statusDot: View
    private lateinit var stateTag: TextView
    private lateinit var status: TextView
    private lateinit var city: TextView
    private lateinit var temp: TextView
    private lateinit var weather: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_weather)
        setToolbar(R.string.weather_title, "MVC")
        cityEdit = findViewById(R.id.city_edit)
        failSwitch = findViewById(R.id.fail_switch)
        progress = findViewById(R.id.progress)
        statusCard = findViewById(R.id.status_card)
        statusDot = findViewById(R.id.status_dot)
        stateTag = findViewById(R.id.state_tag)
        status = findViewById(R.id.status)
        city = findViewById(R.id.city)
        temp = findViewById(R.id.temp)
        weather = findViewById(R.id.weather)
        findViewById<Button>(R.id.query_button).setOnClickListener { query() }
        showIdle()
    }

    private fun query() {
        job?.cancel()
        showLoading()
        val simulateFailure = failSwitch.isChecked
        job = lifecycleScope.launch {
            model.getWeather(cityEdit.text.toString(), simulateFailure).fold(::showWeather) { showError() }
        }
    }

    private fun showIdle() {
        render(Phase.IDLE, R.string.idle, null)
    }

    private fun showLoading() {
        render(Phase.LOADING, R.string.loading, null)
    }

    private fun showWeather(info: WeatherInfo) {
        render(Phase.SUCCESS, R.string.success, info)
    }

    private fun showError() {
        render(Phase.ERROR, R.string.failure, null)
    }

    private fun render(phase: Phase, message: Int, info: WeatherInfo?) {
        progress.visibility = if (phase == Phase.LOADING) View.VISIBLE else View.GONE
        status.setText(message)
        applyPhase(statusCard, statusDot, stateTag, phase)
        city.text = info?.city.orEmpty()
        temp.text = info?.temp.orEmpty()
        weather.text = info?.weather.orEmpty()
    }
}
