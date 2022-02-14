// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import api.AppListItem
import com.jediterm.terminal.TtyConnector
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun App() {
    MaterialTheme {
        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
        )
        val coroutineScope = rememberCoroutineScope()
        var ttyConnector by remember { mutableStateOf<TtyConnector?>(null) }
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                Column {
                    Spacer(modifier = Modifier.height(32.dp))
                    TerminalWrapper(
                        modifier = Modifier.fillMaxWidth()
                            .height(300.dp),
                        ttyConnectorFactory = { createTtyConnector() }
                    )
                }

            }, sheetPeekHeight = 0.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f)
                ) {
                    val detailData = remember { mutableStateOf<Pair<String, AppListItem>?>(null) }
                    val data = detailData.value
                    if (data != null) {
                        AppDetailPage(data.first, data.second) {
                            detailData.value = null
                        }
                    } else {
                        CategoryBrowse { category, item ->
                            detailData.value = category to item
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.End)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                    bottomSheetScaffoldState.bottomSheetState.expand()
                                } else {
                                    bottomSheetScaffoldState.bottomSheetState.collapse()
                                }
                            }
                        }) {
                        Text("Terminal")
                    }
                    Spacer(Modifier.width(16.dp))
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = {
            exitApplication()
        },
        title = "Spark Compose",
    ) {
        App()
    }
}