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
package com.wx.mvvm.viewmodel;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.wx.mvvm.databinding.LayoutWeatherMvvmBinding;
import com.wx.mvvm.model.WeatherModel;
import com.wx.mvvm.model.entity.WeatherInfo;
import com.wx.mvvm.model.impl.WeatherModelImpl;

/**
 * @author venshine
 */
public class WeatherViewModel implements OnWeatherListener {

    private LayoutWeatherMvvmBinding mBinding;
    private WeatherModel mWeatherModel;
    private Context mContext;
    private WeatherInfo mWeatherInfo;

    public WeatherViewModel(Context context, LayoutWeatherMvvmBinding binding) {
        mContext = context;
        mBinding = binding;
        mWeatherModel = new WeatherModelImpl();
        mWeatherInfo = new WeatherInfo();
        mBinding.setWeather(mWeatherInfo);
    }

    public void query(View view) {
        mWeatherInfo.clear();
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mWeatherModel.getWeather(mBinding.cityEdit.getText().toString(), this);
    }

    @Override
    public void onSucc(WeatherInfo weatherInfo) {
        Toast.makeText(mContext, "加载成功", Toast.LENGTH_SHORT).show();
        mBinding.progressBar.setVisibility(View.GONE);
        mWeatherInfo.setWeatherInfo(weatherInfo);
    }

    @Override
    public void onFail() {
        Toast.makeText(mContext, "加载失败", Toast.LENGTH_SHORT).show();
        mBinding.progressBar.setVisibility(View.GONE);
    }

}
