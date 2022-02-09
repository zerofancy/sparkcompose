// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    val mainViewModel = MainViewModel()
    var text by remember { mutableStateOf("Hello, World!") }

    val appList = mainViewModel.appList.collectAsState()

    MaterialTheme {
        val scrollState = rememberScrollState()
        Box(modifier = Modifier.fillMaxSize()) {
            Row {
                val mainLazyListState = rememberLazyListState()
                LazyColumn(
                    modifier = Modifier,
                    state = mainLazyListState,
                ) {
                    item {
                        Button(onClick = {
                            mainViewModel.switchList()
                        }) {
                            Text(text)
                        }
                    }
                    items(appList.value.size) { index ->
                        Text(appList.value[index].name)
                    }
                    item {
                        Text("已经是最后了～～～～～～～～～～～～～～～")
                    }
                }
                // 这些信息加起来能实现一个简单滚动条
                Text(mainLazyListState.firstVisibleItemIndex.toString())
                Spacer(Modifier.width(4.dp))
                Text(mainLazyListState.layoutInfo.visibleItemsInfo.size.toString())
                Spacer(Modifier.width(4.dp))
                Text(mainLazyListState.layoutInfo.totalItemsCount.toString())

                Spacer(Modifier.width(4.dp))
                Text(mainLazyListState.firstVisibleItemScrollOffset.toString())
                Spacer(Modifier.width(4.dp))
                Text(mainLazyListState.layoutInfo.viewportStartOffset.toString())
                Spacer(Modifier.width(4.dp))
                Text(mainLazyListState.layoutInfo.viewportEndOffset.toString())
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = {
        exitApplication()
    }) {
        App()
    }
}
