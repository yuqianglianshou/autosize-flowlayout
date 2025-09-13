package com.ice.flowlayout.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ice.flowlayout.components.AutoSizeFlowLayout

/**
 *
 *@author : lq
 *@date   : 2025/9/6
 *@desc   : 页面 0  基础使用
 *
 */
@Composable
fun Tab0Screen() {
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

            Text(text = "示例：AutoSizeFlowLayout, 每行标签自动拉伸平分宽度，支持范型参数列表。")

            AutoSizeFlowLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                dataList = tagList,
                label = { it },
                horizontalGap = 8.dp,
                verticalGap = 12.dp,
                onItemClick = { index, item ->
                    Toast.makeText(
                        context,
                        "点击了 第 $index 条数据，内容是：$item",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )


        }
    }
}