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
package com.wx.mvp.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.mvp.R;
import com.wx.mvp.model.entity.WeatherInfo;
import com.wx.mvp.presenter.WeatherPresenter;
import com.wx.mvp.presenter.impl.WeatherPresenterImpl;

/**
 * @author venshine
 */
public class WeatherActivity extends AppCompatActivity implements WeatherView {

    private EditText mCityEdit;

    private ProgressBar mProgressBar;

    private TextView mCity, mTemp, mWeather;

    private WeatherPresenter mWeatherPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_weather);
        initView();
        mWeatherPresenter = new WeatherPresenterImpl(this);
    }

    private void initView() {
        mCityEdit = (EditText) findViewById(R.id.city_edit);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mCity = (TextView) findViewById(R.id.city);
        mTemp = (TextView) findViewById(R.id.temp);
        mWeather = (TextView) findViewById(R.id.weather);
    }

    public void query(View view) {
        mWeatherPresenter.getWeather(mCityEdit.getText().toString());
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mCity.setText("");
        mTemp.setText("");
        mWeather.setText("");
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError() {
        Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setWeatherInfo(WeatherInfo weatherInfo) {
        mCity.setText(weatherInfo.getCity());
        mTemp.setText(weatherInfo.getTemp());
        mWeather.setText(weatherInfo.getWeather());
    }
}
