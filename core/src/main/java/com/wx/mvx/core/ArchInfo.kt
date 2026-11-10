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

import androidx.annotation.StringRes

/**
 * 一种架构的首页文案（字符串资源 id）。文案放在各架构模块自己的 strings.xml，
 * 共享的首页（View 版 [ArchHomeActivity]、Compose 版同名类）只负责排版。
 *
 * @author venshine
 */
data class ArchInfo(
    @StringRes val name: Int,
    @StringRes val flow: Int,
    @StringRes val roleView: Int,
    @StringRes val roleMid: Int,
    @StringRes val roleModel: Int,
    @StringRes val title: Int,
    @StringRes val desc: Int,
    @StringRes val roleViewDesc: Int,
    @StringRes val roleMidDesc: Int,
    @StringRes val roleModelDesc: Int,
)
