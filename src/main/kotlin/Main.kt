// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
fun App() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            val detailData = remember { mutableStateOf<Pair<String, AppListItem>?>(null) }
            val data = detailData.value
            if (data!=null) {
                AppDetailPage(data.first, data.second) {
                    detailData.value = null
                }
            } else {
                CategoryBrowse { category, item ->
                    detailData.value = category to item
                }
            }
            // 这种写法有问题，但我不知道为什么 https://github.com/JetBrains/compose-jb/issues/1830
//            data?.let {
//                AppDetailPage(it)
//            }?: kotlin.run {
//                CategoryBrowse {
//                    detailData.value = it
//                }
//            }
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
