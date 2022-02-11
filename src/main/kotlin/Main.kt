// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: T? by produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            tryOrNull { load() }
        }
    }
    Image(
        painter = image?.let {
            painterFor(it)
        } ?: run {
            BitmapPainter(
                ImageBitmap(
                    1,
                    1
                )
            )
        },
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}

fun loadImageBitmap(file: File): ImageBitmap =
    file.inputStream().buffered().use(::loadImageBitmap)

fun loadImageBitmap(url: String): ImageBitmap =
    URL(url).openStream().buffered().use(::loadImageBitmap)


@Composable
@Preview
fun StoreItemCard(
    appIcon: String? = null,
    appName: String = "应用名",
    description: String = "",
    onClick: (() -> Unit)? = null
) {
    Box {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp, 16.dp, 8.dp)
            .background(MaterialTheme.colors.background, RoundedCornerShape(4.dp))
        ) {
            AsyncImage(
                load = {
                    tryOrNull {
                        appIcon?.let { loadImageBitmap(it) }
                    }
                },
                painterFor = {
                    remember {
                        BitmapPainter(
                            it!!
                        )
                    }
                },
                contentDescription = "$appName 的应用图标",
                modifier = Modifier.width(60.dp).height(60.dp).background(MaterialTheme.colors.surface)
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(appName, style = MaterialTheme.typography.h5)
                Text(description)
            }
        }
    }
}

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
                        val item = appList.value[index]
                        StoreItemCard(
                            appIcon = item.icons,
                            appName = item.name,
                            description = item.description,
                        )
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
