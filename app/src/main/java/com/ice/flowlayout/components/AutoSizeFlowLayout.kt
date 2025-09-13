package com.ice.flowlayout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 *
 *@author : lq
 *@date   : 2025/9/6
 *@desc   : 一个自适应item宽度的流式布局组件 AutoSizeFlowLayout
 *
 * 支持任意类型的数据列表 dataList，通过 label 函数将数据转换为显示文本。
 *
 * 一般使用场景：标签云、搜索历史、商品属性展示等。
 */
@Composable
fun <T> AutoSizeFlowLayout(
    modifier: Modifier = Modifier,
    dataList: List<T>,//要显示的数据列表
    label: (T) -> String = { it.toString() },//提供一个 label 转换函数
    horizontalGap: Dp = 8.dp,// 水平间距，默认 8.dp
    verticalGap: Dp = 8.dp,// 垂直间距，默认 8.dp
    textStyle: TextStyle = TextStyle(fontSize = 14.sp),// 文本样式，默认 14.sp
    onItemClick: (index: Int, item: T) -> Unit = { _, _ -> }// 点击回调函数
) {
    Layout(
        content = {
            dataList.forEachIndexed { index, item ->
                //圆角背景、可点击  的 textview
                Text(
                    text = label(item),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .clickable { onItemClick(index, item) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    style = textStyle,
                    textAlign = TextAlign.Center
                )
            }
        },
        modifier = modifier
    ) { measurables, constraints ->

        val hGapPx = horizontalGap.roundToPx()
        val vGapPx = verticalGap.roundToPx()
        val maxWidth = constraints.maxWidth

        require(maxWidth != Constraints.Infinity) { "父容器必须有最大宽度，例如 fillMaxWidth()" }

        data class Info(val measurable: Measurable, val width: Int, val height: Int)

        // 1. 用 intrinsic 宽度估算标签大小
        val infos = measurables.map { m ->
            val w = m.maxIntrinsicWidth(Constraints.Infinity)
            val h = m.minIntrinsicHeight(w)
            Info(m, w, h)
        }

        // 2. 按行分组，初步规划所有数据 将会分成 多少行，每行都有哪些数据。
        val rows = mutableListOf<MutableList<Info>>()//存储划分之后的 所有数据， rows.size 即行数。
        var curRow = mutableListOf<Info>()
        var curRowWidth = 0

        for (info in infos) {
            val needed = if (curRow.isEmpty()) info.width else curRowWidth + hGapPx + info.width
            if (needed > maxWidth && curRow.isNotEmpty()) {
                rows.add(curRow)
                curRow = mutableListOf()
                curRowWidth = 0
            }
            if (curRow.isNotEmpty()) curRowWidth += hGapPx
            curRow.add(info)
            curRowWidth += info.width
        }
        if (curRow.isNotEmpty()) rows.add(curRow)

        // 3. 真正测量，每行的额外空间 被重新分配
        val rowPlaceables: List<List<Placeable>> = rows.mapIndexed { rowIndex, row ->
            //count 当前行中的数据个数
            val count = row.size
            if (count == 0) return@mapIndexed emptyList()

            //totalGap 总边距
            val totalGap = hGapPx * (count - 1)
            //contentWidth 所有内容宽度
            val contentWidth = row.sumOf { it.width }
            //extraWidth 当前行额外宽度
            val extraWidth =
                if (rowIndex == rows.lastIndex) 0 else maxWidth - totalGap - contentWidth
            //extraPerItem 每行额外宽度被 均分；   remainder 余数
            //像素不能取小数，比如 extraWidth = 27，view个数 count = 10，那么分配策略为
            // 先是每个 view 分配 extraPerItem =2px，余数 remainder = 7 分配到前 7个view，每个再分配 1px。
            // 最终分配结果为 3 3 3 3 3 3 3 2 2 2
            val extraPerItem = extraWidth / count
            val remainder = extraWidth % count

            row.mapIndexed { idx, info ->
                // 计算每个view的实际宽度：原有宽度 + 额外宽度被均分后的宽度 + 余数（只有前 remainder 个有 1px ）
                val targetWidth = info.width + extraPerItem + if (idx < remainder) 1 else 0
                info.measurable.measure(Constraints.fixedWidth(targetWidth))
            }
        }

        // 4. 计算总高度
        val rowHeights = rowPlaceables.map { it.maxOfOrNull { p -> p.height } ?: 0 }
        val layoutHeight = rowHeights.sum() + vGapPx * (rowHeights.size - 1).coerceAtLeast(0)

        // 5. 摆放
        layout(width = maxWidth, height = layoutHeight) {
            var y = 0
            rowPlaceables.forEachIndexed { rowIndex, row ->
                var x = 0
                val rowH = rowHeights[rowIndex]
                row.forEach { p ->
                    p.placeRelative(x, y)
                    x += p.width + hGapPx
                }
                y += rowH + vGapPx
            }
        }
    }
}


