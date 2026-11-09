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

import com.wx.mvp.model.entity.WeatherInfo

/** View 接口：Presenter 只认识它，不认识 Activity 和任何 Android 类 */
interface WeatherView {
    fun showLoading()
    fun showWeather(info: WeatherInfo)
    fun showError()
}
