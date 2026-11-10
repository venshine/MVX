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


/**
 * 一次性 Effect：不属于状态、只消费一次的事件（Toast、导航等）。
 * 旋转后不应重放，所以不放进 State，而是走单独的 Channel。
 */
sealed interface WeatherEffect {
    /** 查询失败，提示一次 */
    data object ShowFailure : WeatherEffect
}
