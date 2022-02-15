import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
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

fun loadImageBitmap(file: File?): ImageBitmap? =
    tryOrNull {
        file?:return@tryOrNull null
        file.inputStream().buffered().use(::loadImageBitmap)
    }

fun loadImageBitmap(url: String?): ImageBitmap? = tryOrNull {
    URL(url).openStream().buffered().use(::loadImageBitmap)
}
