package com.ice.flowlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ice.flowlayout.screens.Tab0Screen
import com.ice.flowlayout.screens.Tab1Screen
import com.ice.flowlayout.screens.Tab2Screen
import com.ice.flowlayout.screens.Tab3Screen
import com.ice.flowlayout.ui.theme.AutoSizeFlowlayout
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoSizeFlowlayout {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun HomeScreen(modifier: Modifier = Modifier) {
        //创建页面标题
        val tabTitles = remember {
            List(10) { index -> "Tab $index" }
        }

        // 创建Pager状态
        val pagerState = rememberPagerState(initialPage = 0) { tabTitles.size }
        val coroutineScope = rememberCoroutineScope()

        // 用于跟踪选中的标签索引
        var selectedTabIndex by remember { mutableIntStateOf(pagerState.currentPage) }

        // 当Pager页面改变时更新选中的标签
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .distinctUntilChanged()
                .collect { page ->
                    selectedTabIndex = page
                }
        }
        Column(modifier = modifier.fillMaxSize()) {
            // 顶部标签栏
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = title, maxLines = 1) }
                    )
                }
            }

            // 水平滑动页面
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) { page ->
                // 每个页面的内容
                FragmentPage(pageIndex = page)
            }
        }
    }

    @Composable
    fun FragmentPage(pageIndex: Int, modifier: Modifier = Modifier) {

        when (pageIndex) {
            0 -> {
                Tab0Screen()
            }

            1 -> {
                Tab1Screen()
            }

            2 -> {
                Tab2Screen()
            }

            3 -> {
                Tab3Screen()
            }

            else -> {
                // 模拟Fragment页面内容
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Fragment 页面 ${pageIndex + 1}",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "\n这是第 ${pageIndex + 1} 个页面的内容，" +
                                "类似于一个 Fragment。\n\n" +
                                "可以在这里放置任何你想要的 UI 组件，" +
                                "就像在传统的 Fragment 中一样。",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }


    }

    @Preview(showBackground = true)
    @Composable
    fun HomeScreenPreview() {
        AutoSizeFlowlayout {
            HomeScreen()
        }
    }
}