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
package com.wx.mvp

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/*
 * 示例页共用的小工具：取色、单位换算，以及天气页的状态样式
 */

@ColorInt
fun Context.colorOf(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

fun Context.dp(value: Float): Int = (value * resources.displayMetrics.density + 0.5f).toInt()

/**
 * 天气页的四种展示阶段，只管颜色和标签，不含业务判断。
 * 三个 App 各自把自己的状态（回调 / UiState）映射到它，界面外观因此完全一致。
 */
enum class Phase(
    val tag: String,
    @ColorRes val tint: Int,
    @ColorRes val ink: Int,
    @ColorRes val soft: Int,
) {
    IDLE("IDLE", R.color.text_dim, R.color.text_dim, R.color.surface),
    LOADING("LOADING", R.color.accent, R.color.accent_ink, R.color.accent_soft),
    SUCCESS("SUCCESS", R.color.ok, R.color.ok_ink, R.color.ok_soft),
    ERROR("ERROR", R.color.err, R.color.err_ink, R.color.err_soft),
}

/** 按阶段给状态卡片换底色和描边，圆点和标签换成同色系，状态切换一眼可见 */
fun applyPhase(card: View, dot: View, tag: TextView, phase: Phase) {
    val context = card.context
    card.background = GradientDrawable().apply {
        cornerRadius = context.dp(20f).toFloat()
        setColor(context.colorOf(phase.soft))
        val stroke = if (phase == Phase.IDLE) R.color.border else phase.tint
        setStroke(context.dp(if (phase == Phase.IDLE) 1f else 1.5f), context.colorOf(stroke))
    }
    dot.background = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(context.colorOf(phase.tint))
    }
    tag.text = phase.tag
    tag.setTextColor(context.colorOf(phase.ink))
}
