package com.ice.flowlayout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
 * 多选模式 流式布局
 */
@Composable
fun <T> AutoSizeFlowLayoutMultiSelect(
    modifier: Modifier = Modifier,
    dataList: List<T>,
    label: (T) -> String = { it.toString() },
    horizontalGap: Dp = 8.dp,
    verticalGap: Dp = 8.dp,
    textStyle: TextStyle = TextStyle(fontSize = 14.sp),
    defaultSelectedIndexes: Set<Int> = emptySet(), // 默认选中的下标集合
    selectedBgColor: Color = Color.Blue,
    unselectedBgColor: Color = Color.LightGray,
    selectedTextColor: Color = Color.White,
    unselectedTextColor: Color = Color.Black,
    onSelectionChanged: (selectedIndexes: Set<Int>, selectedItems: List<T>) -> Unit
) {
    // 记录当前选中的索引集合
    // 使用不可变 Set 做状态，重新赋值会触发 Compose 更新
    var selectedIndexes by remember { mutableStateOf(defaultSelectedIndexes.toSet()) }


    Layout(
        content = {
            dataList.forEachIndexed { index, item ->
                val isSelected = index in selectedIndexes
                Text(
                    text = label(item),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) selectedBgColor else unselectedBgColor)
                        .clickable {
                            val newSet =
                                if (isSelected) selectedIndexes - index else selectedIndexes + index
                            selectedIndexes = newSet
                            onSelectionChanged(newSet, newSet.map { dataList[it] })
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
            Info(m, w, h)
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
