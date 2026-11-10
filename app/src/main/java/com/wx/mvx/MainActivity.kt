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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.wx.mvx.core.BaseActivity
import com.wx.mvx.core.R as CoreR
import com.wx.mvx.core.openSource
import com.wx.mvx.core.versionTag

/**
 * 首页：六种架构的入口卡片和对比页入口。点进去才是各架构模块自己的页面。
 *
 * @author venshine
 */
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hub)
        findViewById<View>(R.id.source_button).setOnClickListener { openSource() }
        findViewById<TextView>(R.id.version_chip).text = versionTag()

        val compare = findViewById<View>(R.id.card_compare)
        compare.findViewById<ImageView>(CoreR.id.card_icon).setImageResource(CoreR.drawable.ic_code)
        compare.findViewById<TextView>(R.id.card_title).setText(R.string.compare_title)
        compare.findViewById<TextView>(R.id.card_desc).setText(R.string.compare_card_desc)
        compare.setOnClickListener { startActivity(Intent(this, CompareActivity::class.java)) }

        val list = findViewById<LinearLayout>(R.id.arch_list)
        val inflater = LayoutInflater.from(this)
        architectures.forEach { arch ->
            val card = inflater.inflate(R.layout.item_arch_card, list, false)
            card.tag = arch.key
            card.findViewById<TextView>(R.id.card_title).setText(arch.name)
            card.findViewById<TextView>(R.id.card_desc).setText(arch.tagline)
            card.findViewById<TextView>(R.id.card_tag).setText(if (arch.compose) R.string.tag_compose else R.string.tag_view)
            card.setOnClickListener { startActivity(Intent(this, arch.entry)) }
            list.addView(card)
        }
    }
}
