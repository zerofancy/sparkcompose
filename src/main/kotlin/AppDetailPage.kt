import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
@Preview
private fun AppDetailPagePreview() = AppDetailPage("", AppListItem(), {})

@Composable
fun AppDetailPage(category: String, data: AppListItem, onBackPrevious: () -> Unit) {
    Column {
        Button(onBackPrevious) {
            Text("返回")
        }
        Button(onClick = {
            openUrlInBrowser(data.website)
        }) {
            Text("网站")
        }
        Button(
            onClick = {
                openUrlInBrowser("https://d.store.deepinos.org.cn/store/$category/${data.packageName}/${data.fileName}")
            }
        ) {
            Text("下载")
        }
    }
}