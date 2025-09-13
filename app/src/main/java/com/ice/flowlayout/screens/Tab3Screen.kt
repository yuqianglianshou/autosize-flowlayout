package com.ice.flowlayout.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ice.flowlayout.components.AutoSizeFlowLayoutMultiSelect

/**
 *
 *@author : lq
 *@date   : 2025/9/6
 *@desc   : 页面 0  基础使用
 *
 */
@Composable
fun Tab3Screen() {
    val context = LocalContext.current
    val tagList = listOf(
        "标签1",
        "标签2",
        "标签33",
        "标签4!",
        "长标签测试",
        "很长很长的标签文字",
        "A+",
        "B",
        "C5",
        "D",
        "标签一",
        "长标签二二二二二二",
        "标签三",
        "中等长度标签",
        "A",
        "B+",
        "超级长标签文字测试"
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(text = "示例：AutoSizeFlowLayoutMultiSelect, 多选模式演示。")

            AutoSizeFlowLayoutMultiSelect(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                dataList = tagList,
                label = { it },
                horizontalGap = 8.dp,
                verticalGap = 12.dp,
                defaultSelectedIndexes = setOf(1, 3),// 默认选中第 2个，第四个
                selectedBgColor = Color(0xFF008577),
                unselectedBgColor = Color.LightGray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.DarkGray,
                onSelectionChanged = { indexes, items ->
                    Toast.makeText(
                        context,
                        "选中的下标:  $indexes  ，选中的数据：$items",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )


        }
    }
}