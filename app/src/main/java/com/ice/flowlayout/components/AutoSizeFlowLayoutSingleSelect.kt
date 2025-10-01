package com.ice.flowlayout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * 单选模式 流式布局
 */
@Composable
fun <T> AutoSizeFlowLayoutSingleSelect(
    modifier: Modifier = Modifier,
    dataList: List<T>,
    label: (T) -> String = { it.toString() },
    horizontalGap: Dp = 8.dp,
    verticalGap: Dp = 8.dp,
    textStyle: TextStyle = TextStyle(fontSize = 14.sp),
    defaultSelectedIndex: Int = -1, // 默认选中下标，-1 表示不选
    selectedBgColor: Color = Color.Blue,
    unselectedBgColor: Color = Color.LightGray,
    selectedTextColor: Color = Color.White,
    unselectedTextColor: Color = Color.Black,
    onSelected: (index: Int, item: T) -> Unit
) {
    // 记录当前选中的索引
    var selectedIndex by remember { mutableIntStateOf(defaultSelectedIndex) }

    Layout(
        content = {
            dataList.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                Text(
                    text = label(item),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) selectedBgColor else unselectedBgColor)
                        .clickable {
                            selectedIndex = index
                            onSelected(index, item)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    style = textStyle.copy(color = if (isSelected) selectedTextColor else unselectedTextColor),
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

        val infos = measurables.map { m ->
            val w = m.maxIntrinsicWidth(Constraints.Infinity)
            val h = m.minIntrinsicHeight(w)
            // 单个 item 的宽度不能超过 maxWidth，否则无法触发 ellipsis
            val finalW = minOf(w, maxWidth)
            Info(m, finalW, h)
        }

        val rows = mutableListOf<MutableList<Info>>()
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

        val rowPlaceables: List<List<Placeable>> = rows.mapIndexed { rowIndex, row ->
            val count = row.size
            if (count == 0) return@mapIndexed emptyList()

            val totalGap = hGapPx * (count - 1)
            val contentWidth = row.sumOf { it.width }
            val extraWidth =
                if (rowIndex == rows.lastIndex) 0 else maxWidth - totalGap - contentWidth
            val extraPerItem = extraWidth / count
            val remainder = extraWidth % count

            row.mapIndexed { idx, info ->
                val targetWidth = info.width + extraPerItem + if (idx < remainder) 1 else 0
                info.measurable.measure(Constraints.fixedWidth(targetWidth))
            }
        }

        val rowHeights = rowPlaceables.map { it.maxOfOrNull { p -> p.height } ?: 0 }
        val layoutHeight = rowHeights.sum() + vGapPx * (rowHeights.size - 1).coerceAtLeast(0)

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
