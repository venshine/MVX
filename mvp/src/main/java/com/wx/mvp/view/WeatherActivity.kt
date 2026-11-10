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
import androidx.appcompat.widget.SwitchCompat
import com.wx.mvp.presenter.WeatherPresenter
import com.wx.mvx.core.BaseActivity
import com.wx.mvx.core.Phase
import com.wx.mvx.core.R
import com.wx.mvx.core.applyPhase
import com.wx.mvx.core.model.entity.WeatherInfo
import com.wx.mvx.core.model.impl.WeatherModelImpl
import kotlinx.coroutines.Dispatchers

/**
 * MVP 的 View：只负责展示和转发点击，业务逻辑都在 [WeatherPresenter]。
 *
 * @author venshine
 */
internal class WeatherActivity : BaseActivity(), WeatherView {

    private val presenter = WeatherPresenter(WeatherModelImpl(), Dispatchers.Main.immediate)

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
        setToolbar(R.string.weather_title, "MVP")
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
        findViewById<Button>(R.id.query_button).setOnClickListener {
            presenter.getWeather(cityEdit.text.toString(), failSwitch.isChecked)
        }
        presenter.attach(this)
        render(Phase.IDLE, R.string.idle, null)
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    override fun showLoading() {
        render(Phase.LOADING, R.string.loading, null)
    }

    override fun showWeather(info: WeatherInfo) {
        render(Phase.SUCCESS, R.string.success, info)
    }

    override fun showError() {
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
