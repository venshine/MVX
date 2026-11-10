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
package com.wx.mvi.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wx.mvi.mvi.Status
import com.wx.mvi.mvi.WeatherEffect
import com.wx.mvi.mvi.WeatherIntent
import com.wx.mvi.mvi.WeatherState
import com.wx.mvi.mvi.WeatherViewModel
import com.wx.mvx.core.BaseActivity
import com.wx.mvx.core.Phase
import com.wx.mvx.core.R
import com.wx.mvx.core.applyPhase
import com.wx.mvx.core.databinding.LayoutWeatherBinding
import com.wx.mvx.core.model.impl.WeatherModelImpl
import kotlinx.coroutines.launch

/**
 * MVI 的 View：订阅 State 渲染界面、消费 Effect，用户操作只翻译成 Intent 发给 ViewModel。
 *
 * @author venshine
 */
internal class WeatherActivity : BaseActivity() {

    private lateinit var binding: LayoutWeatherBinding

    private val viewModel: WeatherViewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory { initializer { WeatherViewModel(WeatherModelImpl()) } },
        )[WeatherViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar(R.string.weather_title, "MVI")
        binding.queryButton.setOnClickListener {
            viewModel.dispatch(WeatherIntent.Query(binding.cityEdit.text.toString(), binding.failSwitch.isChecked))
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.state.collect(::render) }
                launch { viewModel.effects.collect(::handle) }
            }
        }
    }

    private fun handle(effect: WeatherEffect) {
        when (effect) {
            WeatherEffect.ShowFailure -> Toast.makeText(this, com.wx.mvi.R.string.failure_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun render(state: WeatherState) {
        val phase = when (state.status) {
            Status.IDLE -> Phase.IDLE
            Status.LOADING -> Phase.LOADING
            Status.SUCCESS -> Phase.SUCCESS
            Status.ERROR -> Phase.ERROR
        }
        binding.progress.visibility = if (phase == Phase.LOADING) View.VISIBLE else View.GONE
        binding.status.setText(
            when (phase) {
                Phase.IDLE -> R.string.idle
                Phase.LOADING -> R.string.loading
                Phase.SUCCESS -> R.string.success
                Phase.ERROR -> R.string.failure
            },
        )
        applyPhase(binding.statusCard, binding.statusDot, binding.stateTag, phase)
        binding.city.text = state.info?.city.orEmpty()
        binding.temp.text = state.info?.temp.orEmpty()
        binding.weather.text = state.info?.weather.orEmpty()
    }
}
