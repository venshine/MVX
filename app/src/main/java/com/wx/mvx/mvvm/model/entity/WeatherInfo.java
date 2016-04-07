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
package com.wx.mvx.mvvm.model.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.wx.mvx.BR;

/**
 * 实体模型层
 *
 * @author venshine
 */
public class WeatherInfo extends BaseObservable {

    private String city;
    private String temp;
    private String weather;

    public WeatherInfo() {

    }

    public WeatherInfo(String city, String temp, String weather) {
        setCity(city);
        setTemp(temp);
        setWeather(weather);
    }

    public void setWeatherInfo(WeatherInfo weatherInfo) {
        setCity(weatherInfo.getCity());
        setTemp(weatherInfo.getTemp());
        setWeather(weatherInfo.getWeather());
    }

    public void clear() {
        setCity("");
        setTemp("");
        setWeather("");
    }

    @Bindable
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        notifyPropertyChanged(BR.city);
    }

    @Bindable
    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
        notifyPropertyChanged(BR.temp);
    }

    @Bindable
    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
        notifyPropertyChanged(BR.weather);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WeatherInfo{");
        sb.append("city='").append(city).append('\'');
        sb.append(", temp='").append(temp).append('\'');
        sb.append(", weather='").append(weather).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
