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
package com.wx.udf.viewmodel

import com.wx.mvx.core.model.entity.WeatherInfo

/**
 * 界面状态（官方写法：`功能 + UiState` 的不可变 data class）。
 * 三个布尔 / 数据字段都为空即「空闲」；View 只根据它渲染，不自己判断业务。
 */
data class WeatherUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val info: WeatherInfo? = null,
)
