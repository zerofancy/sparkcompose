import java.awt.Desktop
import java.net.URI

internal inline fun <T> tryOrNull(block: () -> T): T? =
    try {
        block()
    } catch (e: Exception) {
        null
    }

internal fun openUrlInBrowser(url: String) = tryOrNull {
    Desktop.getDesktop().browse(URI(url))
}