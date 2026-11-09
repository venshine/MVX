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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.wx.mvvm.R
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
class WeatherActivity : AppCompatActivity() {

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
        val root = binding.root
        // targetSdk 35+ 强制 edge-to-edge，给根布局补上左右和底部的系统栏边距
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, 0, bars.right, bars.bottom)
            insets
        }
        binding.queryButton.setOnClickListener { viewModel.query(binding.cityEdit.text.toString()) }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::render)
            }
        }
    }

    private fun render(state: WeatherUiState) {
        binding.progress.visibility = if (state is WeatherUiState.Loading) View.VISIBLE else View.GONE
        when (state) {
            WeatherUiState.Idle -> {
                binding.status.text = ""
                clearWeather()
            }
            WeatherUiState.Loading -> {
                binding.status.setText(R.string.loading)
                clearWeather()
            }
            is WeatherUiState.Success -> {
                binding.status.setText(R.string.success)
                binding.city.text = state.info.city
                binding.temp.text = state.info.temp
                binding.weather.text = state.info.weather
            }
            WeatherUiState.Error -> {
                binding.status.setText(R.string.failure)
                clearWeather()
            }
        }
    }

    private fun clearWeather() {
        binding.city.text = ""
        binding.temp.text = ""
        binding.weather.text = ""
    }
}
