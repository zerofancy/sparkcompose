// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URL

/**
 * @param key 触发compose重组
 */
@Composable
fun <K, T> AsyncImage(
    key: K? = null,
    load: suspend (K?) -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val testImage = remember { mutableStateOf<T?>(null) }
    LaunchedEffect(key) {
        testImage.value = withContext(Dispatchers.IO) {
            tryOrNull { load(key) }
        }
    }
    Image(
        painter = testImage.value?.let {
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
    modifier: Modifier = Modifier,
    appIcon: String? = null,
    appName: String = "应用名",
    description: String = "",
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 8.dp, 16.dp, 8.dp)
                .background(MaterialTheme.colors.background, RoundedCornerShape(4.dp))
        ) {
            AsyncImage(
                key = appIcon,
                load = {
                    tryOrNull {
                        it?.let { it1 -> loadImageBitmap(it1) }
                    }
                },
                painterFor = {
                    BitmapPainter(
                        it!!
                    )
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
    val mainViewModel = remember {
        MainViewModel().apply { switchList(0) }
    }

    val appList = mainViewModel.appList.collectAsState()

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                val categoryListState = rememberLazyListState()
                val scrollDelta = remember { mutableStateOf(0f) }

                LaunchedEffect(scrollDelta.value) {
                    val offset = scrollDelta.value.takeIf { it != 0f } ?: return@LaunchedEffect
                    val consumed = categoryListState.scrollBy(-offset)
                    scrollDelta.value -= consumed
                }
                val categoryDragState = rememberDraggableState { delta ->
                    scrollDelta.value = delta
                }
                LazyRow(
                    modifier = Modifier
                        .draggable(categoryDragState, Orientation.Horizontal),
                    state = categoryListState
                ) {
                    items(mainViewModel.categories.size) { index ->
                        Spacer(modifier = Modifier.width(if (index == 0) 16.dp else 8.dp))
                        Button(
                            onClick = { mainViewModel.switchList(index) },
                            colors = object : ButtonColors {
                                val currentIndex = mainViewModel.currentIndex.collectAsState()

                                @Composable
                                override fun backgroundColor(enabled: Boolean): State<Color> {
                                    return rememberUpdatedState(
                                        if (index == currentIndex.value) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            MaterialTheme.colors.onPrimary
                                        }
                                    )
                                }

                                @Composable
                                override fun contentColor(enabled: Boolean): State<Color> {
                                    return rememberUpdatedState(
                                        if (index == currentIndex.value) {
                                            MaterialTheme.colors.onPrimary
                                        } else {
                                            MaterialTheme.colors.primary
                                        }
                                    )
                                }
                            }
                        ) {
                            Text(mainViewModel.categories[index].second)
                        }
                    }
                }
                val mainLazyListState = rememberLazyListState()
                LazyColumn(
                    modifier = Modifier,
                    state = mainLazyListState,
                ) {
                    items(appList.value.size) { index ->
                        val item = appList.value[index]
                        StoreItemCard(
                            modifier = Modifier.clickable {
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop()
                                        .isSupported(Desktop.Action.BROWSE)
                                ) {
                                    tryOrNull {
                                        Desktop.getDesktop().browse(URI(item.website))
                                    }
                                }
                            },
                            appIcon = item.icons,
                            appName = item.name,
                            description = item.description,
                        )
                    }
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
