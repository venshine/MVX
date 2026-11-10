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
package com.wx.mvi.mvi

import com.wx.mvx.core.model.entity.WeatherInfo

/** 展示阶段：只有四种，互斥 */
enum class Status { IDLE, LOADING, SUCCESS, ERROR }

/**
 * MVI 的 State：整个界面唯一的、不可变的状态对象。
 * View 只根据它渲染；任何变化都产生一个新的 State，而不是改旧对象。
 */
data class WeatherState(
    val status: Status = Status.IDLE,
    val info: WeatherInfo? = null,
)
