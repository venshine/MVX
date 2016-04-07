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
package com.wx.mvx.mvc.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.mvx.R;
import com.wx.mvx.mvc.model.OnWeatherListener;
import com.wx.mvx.mvc.model.entity.WeatherInfo;
import com.wx.mvx.mvc.model.impl.WeatherModelImpl;

/**
 * @author venshine
 */
public class WeatherActivity extends AppCompatActivity implements OnWeatherListener {

    private EditText mCityEdit;

    private ProgressBar mProgressBar;

    private TextView mCity, mTemp, mWeather;

    private WeatherModelImpl mWeatherModelImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_weather);
        initView();
        mWeatherModelImpl = new WeatherModelImpl();
    }

    private void initView() {
        mCityEdit = (EditText) findViewById(R.id.city_edit);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mCity = (TextView) findViewById(R.id.city);
        mTemp = (TextView) findViewById(R.id.temp);
        mWeather = (TextView) findViewById(R.id.weather);
    }

    public void query(View view) {
        mCity.setText("");
        mTemp.setText("");
        mWeather.setText("");
        mProgressBar.setVisibility(View.VISIBLE);
        mWeatherModelImpl.getWeather(mCityEdit.getText().toString(), this);
    }


    @Override
    public void onSucc(WeatherInfo weatherInfo) {
        Toast.makeText(this, "加载成功", Toast.LENGTH_SHORT).show();
        mProgressBar.setVisibility(View.GONE);
        mCity.setText(weatherInfo.getCity());
        mTemp.setText(weatherInfo.getTemp());
        mWeather.setText(weatherInfo.getWeather());
    }

    @Override
    public void onFail() {
        Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
        mProgressBar.setVisibility(View.GONE);
    }
}
