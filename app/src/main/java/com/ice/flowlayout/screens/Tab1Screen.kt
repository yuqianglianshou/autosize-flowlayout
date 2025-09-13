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
import com.ice.flowlayout.bean.User
import com.ice.flowlayout.components.AutoSizeFlowLayout

/**
 *
 *@author : lq
 *@date   : 2025/9/6
 *@desc   : 页面 1  演示 范型参数使用
 *
 */
@Composable
fun Tab1Screen() {
    val context = LocalContext.current
    val studentList = listOf(
        User("张三", 18),
        User("李四1", 19),
        User("王五kdkl", 20),
        User("赵六ddfslsld;dlskd", 18),
        User("钱七", 21),
        User("孙八", 19),
        User("周九", 20),
        User("吴十ddddddd", 22),
        User("郑    一", 18),
        User("王二", 19),
        User("冯三", 20),
        User("陈四   ddd ", 21),
        User("褚五  ", 18),
        User("卫六", 19),
        User("蒋七dsdfs", 20),
        User("沈44444八", 22),
        User("韩5九", 18),
        User("杨十", 19),
        User("朱一类的快乐是快乐的德雷克斯勒打开", 20),
        User("秦二", 21)
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(text = "示例：AutoSizeFlowLayout, 演示 范型参数。")

            AutoSizeFlowLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                dataList = studentList,
                label = { it.name },
                horizontalGap = 8.dp,
                verticalGap = 12.dp,
                onItemClick = { index, item ->
                    Toast.makeText(
                        context,
                        "点击了 第 $index 条数据，内容是：${item}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

        }
    }
}