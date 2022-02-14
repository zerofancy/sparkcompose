import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import api.AppListItem

@Composable
@Preview
private fun AppDetailPagePreview() = AppDetailPage("", AppListItem(), {})

@Composable
fun AppDetailPage(category: String, data: AppListItem, onBackPrevious: () -> Unit) {
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
            Button(
                onClick = {
                    tryOrNull {
                        openUrlInBrowser("https://d.store.deepinos.org.cn/store/$category/${data.packageName}/${data.fileName}")
                    }
                }
            ) {
                Text("下载")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(modifier = Modifier.fillMaxWidth().wrapContentHeight(), text = data.description)
    }
}