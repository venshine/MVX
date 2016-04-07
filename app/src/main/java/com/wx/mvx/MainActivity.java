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
package com.wx.mvx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * @author venshine
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mvcClick(View view) {
        startActivity(new Intent(this, com.wx.mvx.mvc.controller.WeatherActivity.class));
    }

    public void mvpClick(View view) {
        startActivity(new Intent(this, com.wx.mvx.mvp.view.WeatherActivity.class));
    }

    public void mvvmClick(View view) {
        startActivity(new Intent(this, com.wx.mvx.mvvm.view.WeatherActivity.class));
    }

}
