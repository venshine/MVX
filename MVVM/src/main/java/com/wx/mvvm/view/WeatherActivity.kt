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
package com.wx.mvvm.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wx.mvvm.BaseActivity
import com.wx.mvvm.Phase
import com.wx.mvvm.R
import com.wx.mvvm.applyPhase
import com.wx.mvvm.databinding.LayoutWeatherBinding
import com.wx.mvvm.model.impl.WeatherModelImpl
import com.wx.mvvm.viewmodel.WeatherUiState
import com.wx.mvvm.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

/**
 * MVVM 的 View：订阅 [WeatherViewModel.state] 渲染界面，点击转发给 ViewModel。
 * 用 ViewBinding 代替旧版 DataBinding，见 README。
 *
 * @author venshine
 */
class WeatherActivity : BaseActivity() {

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
        setToolbar(R.string.weather_title, "MVVM")
        binding.queryButton.setOnClickListener {
            viewModel.query(binding.cityEdit.text.toString(), binding.failSwitch.isChecked)
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::render)
            }
        }
    }

    private fun render(state: WeatherUiState) {
        val phase = when (state) {
            WeatherUiState.Idle -> Phase.IDLE
            WeatherUiState.Loading -> Phase.LOADING
            is WeatherUiState.Success -> Phase.SUCCESS
            WeatherUiState.Error -> Phase.ERROR
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
        val info = (state as? WeatherUiState.Success)?.info
        binding.city.text = info?.city.orEmpty()
        binding.temp.text = info?.temp.orEmpty()
        binding.weather.text = info?.weather.orEmpty()
    }
}
