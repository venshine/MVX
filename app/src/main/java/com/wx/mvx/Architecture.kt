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
package com.wx.mvx

import android.app.Activity
import androidx.annotation.StringRes
import com.wx.mvc.MainActivity as MvcEntry
import com.wx.mvp.MainActivity as MvpEntry
import com.wx.mvvm.MainActivity as MvvmEntry
import com.wx.mvi.MainActivity as MviEntry
import com.wx.udf.MainActivity as UdfEntry
import com.wx.mv.MainActivity as MvEntry

/**
 * 一种架构在 app 里的登记信息：首页卡片、对比页的文案，以及它的模块入口 Activity。
 * 入口来自各架构库的 MainActivity，app 不认识库里的其他类。
 *
 * @author venshine
 */
class Architecture(
    val key: String,
    val compose: Boolean,
    val entry: Class<out Activity>,
    @StringRes val name: Int,
    @StringRes val tagline: Int,
    @StringRes val duty: Int,
    @StringRes val flow: Int,
    @StringRes val state: Int,
    @StringRes val test: Int,
    @StringRes val fits: Int,
)

val architectures = listOf(
    Architecture(
        key = "mvc",
        compose = false,
        entry = MvcEntry::class.java,
        name = R.string.arch_mvc_name,
        tagline = R.string.arch_mvc_tagline,
        duty = R.string.cmp_mvc_duty,
        flow = R.string.cmp_mvc_flow,
        state = R.string.cmp_mvc_state,
        test = R.string.cmp_mvc_test,
        fits = R.string.cmp_mvc_fits,
    ),
    Architecture(
        key = "mvp",
        compose = false,
        entry = MvpEntry::class.java,
        name = R.string.arch_mvp_name,
        tagline = R.string.arch_mvp_tagline,
        duty = R.string.cmp_mvp_duty,
        flow = R.string.cmp_mvp_flow,
        state = R.string.cmp_mvp_state,
        test = R.string.cmp_mvp_test,
        fits = R.string.cmp_mvp_fits,
    ),
    Architecture(
        key = "mvvm",
        compose = false,
        entry = MvvmEntry::class.java,
        name = R.string.arch_mvvm_name,
        tagline = R.string.arch_mvvm_tagline,
        duty = R.string.cmp_mvvm_duty,
        flow = R.string.cmp_mvvm_flow,
        state = R.string.cmp_mvvm_state,
        test = R.string.cmp_mvvm_test,
        fits = R.string.cmp_mvvm_fits,
    ),
    Architecture(
        key = "mvi",
        compose = false,
        entry = MviEntry::class.java,
        name = R.string.arch_mvi_name,
        tagline = R.string.arch_mvi_tagline,
        duty = R.string.cmp_mvi_duty,
        flow = R.string.cmp_mvi_flow,
        state = R.string.cmp_mvi_state,
        test = R.string.cmp_mvi_test,
        fits = R.string.cmp_mvi_fits,
    ),
    Architecture(
        key = "udf",
        compose = true,
        entry = UdfEntry::class.java,
        name = R.string.arch_udf_name,
        tagline = R.string.arch_udf_tagline,
        duty = R.string.cmp_udf_duty,
        flow = R.string.cmp_udf_flow,
        state = R.string.cmp_udf_state,
        test = R.string.cmp_udf_test,
        fits = R.string.cmp_udf_fits,
    ),
    Architecture(
        key = "mv",
        compose = true,
        entry = MvEntry::class.java,
        name = R.string.arch_mv_name,
        tagline = R.string.arch_mv_tagline,
        duty = R.string.cmp_mv_duty,
        flow = R.string.cmp_mv_flow,
        state = R.string.cmp_mv_state,
        test = R.string.cmp_mv_test,
        fits = R.string.cmp_mv_fits,
    ),
)
