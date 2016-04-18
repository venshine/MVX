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
package com.wx.mvp.model.impl;

import android.os.Handler;
import android.text.TextUtils;

import com.wx.mvp.model.WeatherModel;
import com.wx.mvp.model.entity.WeatherInfo;
import com.wx.mvp.presenter.OnWeatherListener;

/**
 * @author venshine
 */
public class WeatherModelImpl implements WeatherModel {

    @Override
    public void getWeather(final String city, final OnWeatherListener onWeatherListener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(city)) {
                    onWeatherListener.onFail();
                } else {
                    WeatherInfo weatherInfo = new WeatherInfo(city, "18度-25度", "多云转晴");
                    onWeatherListener.onSucc(weatherInfo);
                }
            }
        }, 2000);
    }

}
