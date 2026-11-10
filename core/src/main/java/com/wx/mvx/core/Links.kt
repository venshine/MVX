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
package com.wx.mvx.core

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

const val SOURCE_URL = "https://github.com/venshine/MVX"

/** 用浏览器打开源码仓库；没有浏览器时退化为 Toast 显示地址 */
fun Context.openSource() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL)))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, SOURCE_URL, Toast.LENGTH_LONG).show()
    }
}

/** 版本标签，如 "v2.1.0"；版本号只在 app 模块的 build.gradle 里维护一处 */
fun Context.versionTag(): String = "v" + packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
