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
package com.wx.mvp.model.impl

import com.wx.mvp.model.WeatherModel
import com.wx.mvp.model.entity.WeatherInfo
import kotlinx.coroutines.delay

/**
 * 模拟天气查询：延迟 [delayMillis] 毫秒后，城市为空则失败，否则返回固定数据。
 * 不接真实网络接口；测试里把延迟设为 0 或换成自己的实现即可。
 */
class WeatherModelImpl(private val delayMillis: Long = 2000) : WeatherModel {

    override suspend fun getWeather(city: String): Result<WeatherInfo> {
        delay(delayMillis)
        return if (city.isBlank()) {
            Result.failure(IllegalArgumentException("city is blank"))
        } else {
            Result.success(WeatherInfo(city, "18度-25度", "多云转晴"))
        }
    }
}
