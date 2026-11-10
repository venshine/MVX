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
package com.wx.mvx.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.wx.mvx.core.DemoTheme
import com.wx.mvx.core.R
import com.wx.mvx.core.model.entity.WeatherInfo

/**
 * 天气页的界面部分：无状态，只管把 [phase] / [info] 画出来，把点击交给 [onQuery]。
 * 输入框里的城市和「模拟失败」开关只是界面自己的临时输入，放在这里用 rememberSaveable。
 */
@Composable
fun WeatherContent(
    phase: Phase,
    info: WeatherInfo?,
    chip: String,
    onQuery: (city: String, simulateFailure: Boolean) -> Unit,
    onBack: () -> Unit,
    onToggleTheme: () -> Unit,
) {
    val night = DemoTheme.isNight(LocalContext.current)
    val defaultCity = stringResource(R.string.city_default)
    var city by rememberSaveable { mutableStateOf(defaultCity) }
    var fail by rememberSaveable { mutableStateOf(false) }

    DemoPage {
        // 顶栏：两侧各向外 10dp，让返回图标和页面内容左缘对齐
        Row(
            Modifier
                .layout { measurable, constraints ->
                    val extra = 20.dp.roundToPx()
                    val placeable = measurable.measure(constraints.copy(maxWidth = constraints.maxWidth + extra))
                    layout(constraints.maxWidth, placeable.height) { placeable.place(-extra / 2, 0) }
                }
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBtn(
                R.drawable.ic_back, stringResource(R.string.back), onBack,
                Modifier.testTag("toolbar_back"), padding = 11.dp,
            )
            DemoText(
                stringResource(R.string.weather_title),
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                kind = TextKind.Title,
            )
            Chip(chip)
            IconBtn(
                icon = if (night) R.drawable.ic_sun else R.drawable.ic_moon,
                description = stringResource(if (night) R.string.theme_to_light else R.string.theme_to_dark),
                onClick = onToggleTheme,
                modifier = Modifier.padding(start = 4.dp).testTag("theme_toggle"),
            )
        }

        // 输入：城市 + 模拟失败开关 + 查询
        DemoCard(Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                DemoText(stringResource(R.string.city_label), kind = TextKind.Caption)
                val inputShape = RoundedCornerShape(12.dp)
                BasicTextField(
                    value = city,
                    onValueChange = { city = it },
                    singleLine = true,
                    textStyle = TextStyle(color = colorResource(R.color.text), fontSize = 15.sp),
                    cursorBrush = SolidColor(colorResource(R.color.accent)),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("city_edit")
                        .clip(inputShape)
                        .background(colorResource(R.color.bg))
                        .border(1.dp, colorResource(R.color.line), inputShape)
                        .padding(12.dp),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (city.isEmpty()) {
                                DemoText(
                                    stringResource(R.string.city_hint),
                                    color = colorResource(R.color.text_dim),
                                )
                            }
                            inner()
                        }
                    },
                )
                Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        DemoText(stringResource(R.string.simulate_failure), kind = TextKind.CardTitle)
                        DemoText(
                            stringResource(R.string.simulate_failure_hint),
                            modifier = Modifier.padding(top = 2.dp),
                            kind = TextKind.CardDesc,
                        )
                    }
                    DemoSwitch(
                        checked = fail,
                        onCheckedChange = { fail = it },
                        description = stringResource(R.string.simulate_failure),
                        modifier = Modifier.testTag("fail_switch"),
                    )
                }
                AccentButton(
                    stringResource(R.string.query),
                    onClick = { onQuery(city, fail) },
                    modifier = Modifier.padding(top = 16.dp).testTag("query_button"),
                )
            }
        }

        // 状态卡片：底色、描边、圆点和标签随阶段变化
        val idle = phase == Phase.IDLE
        DemoCard(
            Modifier.fillMaxWidth().padding(top = 16.dp).testTag("status_card"),
            background = colorResource(phase.soft),
            borderColor = colorResource(if (idle) R.color.border else phase.tint),
            borderWidth = if (idle) 1.dp else 1.5.dp,
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth().heightIn(min = 28.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp).clip(CircleShape).background(colorResource(phase.tint)))
                    DemoText(
                        stringResource(
                            when (phase) {
                                Phase.IDLE -> R.string.idle
                                Phase.LOADING -> R.string.loading
                                Phase.SUCCESS -> R.string.success
                                Phase.ERROR -> R.string.failure
                            },
                        ),
                        modifier = Modifier.weight(1f).padding(start = 10.dp).testTag("status"),
                        kind = TextKind.CardTitle,
                    )
                    if (phase == Phase.LOADING) {
                        Spinner(Modifier.padding(end = 10.dp).testTag("progress"))
                    }
                    DemoText(
                        phase.tag,
                        modifier = Modifier.testTag("state_tag"),
                        kind = TextKind.Mono,
                        color = colorResource(phase.ink),
                        letterSpacing = 0.1.em,
                    )
                }
                Column(Modifier.fillMaxWidth().heightIn(min = 132.dp).padding(top = 16.dp)) {
                    DemoText(
                        info?.city.orEmpty(),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 22.dp).testTag("city"),
                        kind = TextKind.Body,
                    )
                    DemoText(
                        info?.temp.orEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp).heightIn(min = 52.dp).testTag("temp"),
                        kind = TextKind.Numeric,
                        fontSize = 40.sp,
                    )
                    DemoText(
                        info?.weather.orEmpty(),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp).heightIn(min = 24.dp).testTag("weather"),
                        fontSize = 17.sp,
                    )
                }
            }
        }
    }
}
