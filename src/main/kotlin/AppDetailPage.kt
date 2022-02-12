import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
@Preview
private fun AppDetailPagePreview() = AppDetailPage(AppListItem())

@Composable
fun AppDetailPage(data: AppListItem) {
    Button(
        onClick = {

        }
    ) {
        Text("下载")
    }
}