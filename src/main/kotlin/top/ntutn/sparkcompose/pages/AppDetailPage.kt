import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.jediterm.terminal.TtyConnector
import kotlinx.coroutines.launch
import top.ntutn.sparkcompose.api.AppListItem
import top.ntutn.sparkcompose.util.GsonUtil
import java.io.File

@Composable
@Preview
private fun AppDetailPagePreview() = AppDetailPage("", AppListItem(), {})

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppDetailPage(
    category: String, data: AppListItem, onBackPrevious: () -> Unit, ttyConnector: TtyConnector? = null,
    terminalState: BottomSheetScaffoldState? = null
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onBackPrevious) {
            Text("返回")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            AsyncImage(
                key = data.icons,
                load = ::loadImageBitmap,
                painterFor = {
                    BitmapPainter(it!!)
                },
                contentDescription = "",
                modifier = Modifier.size(80.dp, 80.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.h6
                )
                if (data.version.isNotBlank()) {
                    Text(
                        text = "版本：${data.version}"
                    )
                }
                if (data.author.isNotBlank()) {
                    Text(
                        text = "作者：${data.author}"
                    )
                }
                if (data.website.isNotBlank()) {
                    Text(
                        text = data.website,
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { tryOrNull { openUrlInBrowser(data.website) } })
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            val coroutineScope = rememberCoroutineScope()
            Button(
                onClick = {
                    tryOrNull {
                        coroutineScope.launch {
                            if (terminalState?.bottomSheetState?.isCollapsed == true) {
                                terminalState.bottomSheetState.expand()
                            }
                            File("/tmp/sparkcompose").mkdirs()
                            ttyConnector?.write("cd /tmp/sparkcompose")
                            ttyConnector?.write("\n")
                            ttyConnector?.write("curl -o ${data.fileName} https://d.store.deepinos.org.cn/store/$category/${data.packageName}/${data.fileName}")
                            ttyConnector?.write("\n")
                            ttyConnector?.write("apt install ./${data.fileName}")
                            ttyConnector?.write("\n")
                        }
//                        openUrlInBrowser("https://d.store.deepinos.org.cn/store/$category/${data.packageName}/${data.fileName}")
                    }
                }
            ) {
                Text("下载")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        val imageURLs = remember { tryOrNull { GsonUtil.gson.fromJson(data.imageUrls, Array<String>::class.java) } }
        LazyColumn {
            item {
                LazyRow(modifier = Modifier.height(300.dp)) {
                    imageURLs?.forEach {
                        item {
                            AsyncImage(
                                key = it,
                                load = ::loadImageBitmap,
                                painterFor = {
                                    BitmapPainter(it!!)
                                },
                                contentDescription = "预览图"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
            item {
                Text(modifier = Modifier.fillMaxWidth().wrapContentHeight(), text = data.description)
            }
        }
    }
}