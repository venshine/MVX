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

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.wx.mvx.core.BaseActivity

/**
 * 对比页：六种架构在职责、数据流、状态位置、可测试性、适用场景上的差异，文案与 README 一致。
 *
 * @author venshine
 */
class CompareActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare)
        setToolbar(R.string.compare_title, "MVX")

        val list = findViewById<LinearLayout>(R.id.compare_list)
        val inflater = LayoutInflater.from(this)
        architectures.forEach { arch ->
            val card = inflater.inflate(R.layout.item_compare_card, list, false)
            card.tag = arch.key
            card.findViewById<TextView>(R.id.cmp_name).setText(arch.name)
            card.findViewById<TextView>(R.id.cmp_kind).setText(if (arch.compose) R.string.tag_compose else R.string.tag_view)
            val rows = card.findViewById<LinearLayout>(R.id.cmp_rows)
            listOf(
                R.string.cmp_label_duty to arch.duty,
                R.string.cmp_label_flow to arch.flow,
                R.string.cmp_label_state to arch.state,
                R.string.cmp_label_test to arch.test,
                R.string.cmp_label_fits to arch.fits,
            ).forEach { (label, value) ->
                val row = inflater.inflate(R.layout.item_compare_row, rows, false)
                row.findViewById<TextView>(R.id.row_label).setText(label)
                row.findViewById<TextView>(R.id.row_value).setText(value)
                rows.addView(row)
            }
            list.addView(card)
        }
    }
}
