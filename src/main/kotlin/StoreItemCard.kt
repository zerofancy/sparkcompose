import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp

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
                modifier = Modifier.width(60.dp).height(60.dp).background(MaterialTheme.colors.surface),
                key = appIcon,
                load = ::loadImageBitmap,
                painterFor = {
                    BitmapPainter(
                        it!!
                    )
                },
                contentDescription = "$appName 的应用图标",
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(appName, style = MaterialTheme.typography.h5)
                Text(description)
            }
        }
    }
}
