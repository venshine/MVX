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
@file:OptIn(ExperimentalComposeUiApi::class)

package com.wx.mvx.compose

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.wx.mvx.core.R

/*
 * Compose 版的外观层：和 View 版共用同一套颜色（res/values 与 values-night）、字体和矢量图标，
 * 只用 foundation 自己实现卡片、按钮、开关、输入框，不引入 Material。
 */

/**
 * 天气页的四种展示阶段，只管颜色和标签，不含业务判断。
 * 六个 App 各自把自己的状态映射到它，界面外观因此完全一致。
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

private val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)
private val JetBrainsMono = FontFamily(Font(R.font.jetbrains_mono))

/** 文字样式，对应 View 版 styles.xml 里的 Text.* */
enum class TextKind { Base, H1, Title, Section, Body, Note, Caption, CardTitle, CardDesc, Mono, Numeric }

@Composable
private fun styleOf(kind: TextKind): TextStyle {
    val text = colorResource(R.color.text)
    val muted = colorResource(R.color.text_muted)
    val dim = colorResource(R.color.text_dim)
    return when (kind) {
        TextKind.Base -> TextStyle(color = text, fontSize = 15.sp)
        TextKind.H1 -> TextStyle(
            color = text, fontSize = 34.sp, fontWeight = FontWeight.Bold,
            fontFamily = SpaceGrotesk, lineHeight = 34.sp * 1.12f,
        )
        TextKind.Title -> TextStyle(color = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        TextKind.Section -> TextStyle(color = text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        TextKind.Body -> TextStyle(color = muted, fontSize = 15.sp, lineHeight = 15.sp * 1.3f)
        TextKind.Note -> TextStyle(color = dim, fontSize = 13.sp, lineHeight = 13.sp * 1.3f)
        TextKind.Caption -> TextStyle(color = dim, fontSize = 12.sp, letterSpacing = 0.08.em)
        TextKind.CardTitle -> TextStyle(color = text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        TextKind.CardDesc -> TextStyle(color = muted, fontSize = 12.sp, lineHeight = 12.sp * 1.3f)
        TextKind.Mono -> TextStyle(
            color = colorResource(R.color.code_text), fontSize = 12.sp, fontFamily = JetBrainsMono,
        )
        TextKind.Numeric -> TextStyle(color = text, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DemoText(
    text: String,
    modifier: Modifier = Modifier,
    kind: TextKind = TextKind.Base,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign = TextAlign.Unspecified,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = styleOf(kind).let { base ->
            base.copy(
                color = if (color.isSpecified) color else base.color,
                fontSize = if (fontSize.isSpecified) fontSize else base.fontSize,
                letterSpacing = if (letterSpacing.isSpecified) letterSpacing else base.letterSpacing,
                textAlign = textAlign,
            )
        },
    )
}

private val Color.isSpecified get() = this != Color.Unspecified
private val TextUnit.isSpecified get() = this != TextUnit.Unspecified

/** 带描边的圆角卡片：对应 bg_card；可选点击 */
@Composable
fun DemoCard(
    modifier: Modifier = Modifier,
    background: Color = colorResource(R.color.surface),
    borderColor: Color = colorResource(R.color.border),
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier
            .clip(shape)
            .background(background)
            .border(borderWidth, borderColor, shape)
            .let { if (onClick != null) it.clickable(role = Role.Button, onClick = onClick) else it },
    ) { content() }
}

/** 强调色小标签：对应 Text.Mono.Chip */
@Composable
fun Chip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            // 合并子节点语义：testTag 落在标签上时，读到的就是标签文字
            .semantics(mergeDescendants = true) {}
            .clip(RoundedCornerShape(6.dp))
            .background(colorResource(R.color.accent_soft))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        DemoText(text, kind = TextKind.Mono, color = colorResource(R.color.accent_ink), fontSize = 11.sp)
    }
}

/** 图标按钮：对应 IconButton / IconButton.Boxed */
@Composable
fun IconBtn(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    boxed: Boolean = false,
    padding: Dp = 12.dp,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier
            .size(44.dp)
            .let {
                if (boxed) it.clip(shape).background(colorResource(R.color.surface))
                    .border(1.dp, colorResource(R.color.line), shape) else it
            }
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }
            .padding(padding),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(colorResource(R.color.text_sub)),
        )
    }
}

/** 主按钮：对应 style Button */
@Composable
fun AccentButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.accent))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DemoText(text, kind = TextKind.Base, color = colorResource(R.color.on_accent), fontSize = 16.sp)
    }
}

/** 页面骨架：背景、避开系统栏、纵向滚动；footer 在内容不足一屏时贴底 */
@Composable
fun DemoPage(
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val scroll: ScrollState = rememberScrollState()
    var contentHeight by remember { mutableIntStateOf(0) }
    var footerHeight by remember { mutableIntStateOf(0) }
    Box(
        modifier
            .fillMaxSize()
            .background(colorResource(R.color.bg))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            // 让 testTag 作为 resource-id 暴露，uiautomator 可按与 View 版相同的 id 查找
            .semantics { testTagsAsResourceId = true },
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val viewport = constraints.maxHeight
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
            ) {
                Column(Modifier.onSizeChanged { contentHeight = it.height }) { content() }
                if (footer != null) {
                    val density = LocalDensity.current
                    // 页面上下内边距共 48dp 也要从视口里扣掉
                    val pad = with(density) { 48.dp.roundToPx() }
                    val gap = (viewport - pad - contentHeight - footerHeight).coerceAtLeast(0)
                    Spacer(Modifier.height(with(density) { gap.toDp() }))
                    Column(Modifier.onSizeChanged { footerHeight = it.height }) { footer() }
                }
            }
        }
    }
}

/** 加载中的小圆环：对应 ProgressBar（小号），用 Canvas 自绘 */
@Composable
fun Spinner(modifier: Modifier = Modifier) {
    val angle by rememberInfiniteTransition(label = "spinner").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "angle",
    )
    val color = colorResource(R.color.accent)
    Canvas(modifier.size(22.dp)) {
        val stroke = 2.5.dp.toPx()
        drawArc(
            color = color,
            startAngle = angle,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(size.width - stroke, size.height - stroke),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

/** 开关：对应 SwitchCompat */
@Composable
fun DemoSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, description: String, modifier: Modifier = Modifier) {
    val track = if (checked) colorResource(R.color.accent) else colorResource(R.color.line)
    Box(
        modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(CircleShape)
            .background(track)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .semantics { contentDescription = description }
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(Modifier.size(20.dp).clip(CircleShape).background(Color.White))
    }
}
