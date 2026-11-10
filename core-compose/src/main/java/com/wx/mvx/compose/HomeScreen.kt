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

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.wx.mvx.core.ArchInfo
import com.wx.mvx.core.DemoTheme
import com.wx.mvx.core.R

/** 首页：本架构的职责划分 + 天气查询入口。无状态，数据和事件都从参数进来 */
@Composable
fun HomeScreen(
    info: ArchInfo,
    version: String,
    onToggleTheme: () -> Unit,
    onSource: () -> Unit,
    onOpenWeather: () -> Unit,
) {
    val night = DemoTheme.isNight(LocalContext.current)
    DemoPage(
        footer = {
            DemoText(
                stringResource(R.string.home_footer),
                modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
                kind = TextKind.Mono,
                color = colorResource(R.color.text_dim),
                textAlign = TextAlign.Center,
            )
        },
    ) {
        // 顶栏
        Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(R.color.accent))
                    .padding(7.dp),
                colorFilter = ColorFilter.tint(colorResource(R.color.on_accent)),
            )
            DemoText(
                stringResource(info.name),
                modifier = Modifier.padding(start = 10.dp),
                kind = TextKind.Numeric,
                fontSize = 19.sp,
            )
            DemoText(
                version,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .testTag("version_chip")
                    .border(1.dp, colorResource(R.color.line), CircleShape)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                kind = TextKind.Mono,
                color = colorResource(R.color.text_muted),
                fontSize = 11.sp,
            )
            Spacer(Modifier.weight(1f))
            IconBtn(
                icon = if (night) R.drawable.ic_sun else R.drawable.ic_moon,
                description = stringResource(if (night) R.string.theme_to_light else R.string.theme_to_dark),
                onClick = onToggleTheme,
                modifier = Modifier.testTag("theme_toggle"),
                boxed = true,
            )
            IconBtn(
                icon = R.drawable.ic_code,
                description = stringResource(R.string.view_source),
                onClick = onSource,
                modifier = Modifier.padding(start = 8.dp).testTag("source_button"),
                boxed = true,
            )
        }

        // 标题
        DemoText(
            stringResource(R.string.home_eyebrow),
            modifier = Modifier.padding(top = 28.dp),
            kind = TextKind.Mono,
            color = colorResource(R.color.accent_ink),
            letterSpacing = 0.14.em,
        )
        DemoText(stringResource(info.title), modifier = Modifier.padding(top = 12.dp), kind = TextKind.H1)
        DemoText(
            stringResource(info.desc),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            kind = TextKind.Body,
        )

        // 职责划分预览
        DemoCard(Modifier.fillMaxWidth().padding(top = 28.dp).testTag("preview_card")) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DemoText(
                        stringResource(R.string.home_preview),
                        modifier = Modifier.weight(1f),
                        color = colorResource(R.color.text_muted),
                        fontSize = 13.sp,
                    )
                    Chip(stringResource(info.flow), Modifier.testTag("flow_text"))
                }
                RoleRow(info.roleView, info.roleViewDesc, "role_view_name", Modifier.padding(top = 16.dp))
                Divider()
                RoleRow(info.roleMid, info.roleMidDesc, "role_mid_name", Modifier.padding(top = 12.dp))
                Divider()
                RoleRow(info.roleModel, info.roleModelDesc, "role_model_name", Modifier.padding(top = 12.dp))
            }
        }

        // 演示入口
        Row(Modifier.fillMaxWidth().padding(top = 32.dp), verticalAlignment = Alignment.Bottom) {
            DemoText(stringResource(R.string.home_demos), kind = TextKind.Section)
            DemoText(
                stringResource(R.string.home_demos_hint),
                modifier = Modifier.weight(1f).padding(start = 10.dp),
                kind = TextKind.Note,
            )
        }
        DemoCard(
            Modifier.fillMaxWidth().padding(top = 14.dp).heightIn(min = 88.dp).testTag("card_weather"),
            onClick = onOpenWeather,
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_cloud),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    colorFilter = ColorFilter.tint(colorResource(R.color.accent_ink)),
                )
                Column(Modifier.weight(1f).padding(start = 16.dp)) {
                    DemoText(stringResource(R.string.demo_weather), kind = TextKind.CardTitle)
                    DemoText(
                        stringResource(R.string.demo_weather_desc),
                        modifier = Modifier.padding(top = 4.dp),
                        kind = TextKind.CardDesc,
                    )
                }
                Image(
                    painter = painterResource(R.drawable.ic_chevron),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp).size(18.dp),
                    colorFilter = ColorFilter.tint(colorResource(R.color.text_dim)),
                )
            }
        }
    }
}

@Composable
private fun RoleRow(@StringRes name: Int, @StringRes desc: Int, tag: String, modifier: Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Chip(stringResource(name), Modifier.width(104.dp).testTag(tag))
        DemoText(
            stringResource(desc),
            modifier = Modifier.weight(1f).padding(start = 12.dp),
            kind = TextKind.CardDesc,
        )
    }
}

@Composable
private fun Divider() {
    Spacer(Modifier.padding(top = 12.dp).fillMaxWidth().height(1.dp).background(colorResource(R.color.border)))
}
