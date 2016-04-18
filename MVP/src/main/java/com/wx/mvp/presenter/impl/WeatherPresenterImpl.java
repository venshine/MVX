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
package com.wx.mvp.presenter.impl;

import com.wx.mvp.model.WeatherModel;
import com.wx.mvp.model.entity.WeatherInfo;
import com.wx.mvp.model.impl.WeatherModelImpl;
import com.wx.mvp.presenter.OnWeatherListener;
import com.wx.mvp.presenter.WeatherPresenter;
import com.wx.mvp.view.WeatherView;

/**
 * @author venshine
 */
public class WeatherPresenterImpl implements WeatherPresenter, OnWeatherListener {

    private WeatherModel mWeatherModel;

    private WeatherView mWeatherView;

    public WeatherPresenterImpl(WeatherView weatherView) {
        mWeatherView = weatherView;
        mWeatherModel = new WeatherModelImpl();
    }

    @Override
    public void getWeather(String city) {
        mWeatherView.showLoading();
        mWeatherModel.getWeather(city, this);
    }

    @Override
    public void onSucc(WeatherInfo weatherInfo) {
        mWeatherView.hideLoading();
        mWeatherView.setWeatherInfo(weatherInfo);
    }

    @Override
    public void onFail() {
        mWeatherView.hideLoading();
        mWeatherView.showError();
    }
}
