import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.TtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.UIUtil
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.pty4j.PtyProcess
import com.pty4j.PtyProcessBuilder
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

@Composable
fun TerminalWrapper(
    modifier: Modifier,
    ttyConnectorFactory: () -> TtyConnector
) {
    var widgetFactory by remember { mutableStateOf<(() -> JediTermWidget)?>(null) }
    // 当用户把terminal关掉时，重新赋值factory以重新创建一个
    var widgetFactory2: (() -> JediTermWidget)? = null
    var factory: (() -> JediTermWidget)? = null
    factory = {
        createTerminalWidget(ttyConnectorFactory, terminalCloseCallback = { widgetFactory = widgetFactory2 })
    }
    widgetFactory2 = {
        createTerminalWidget(ttyConnectorFactory, terminalCloseCallback = { widgetFactory = factory })
    }
    if (widgetFactory == null) {
        println("factory is null, factory = $factory")
        widgetFactory = factory
    }
    SwingPanel(modifier = modifier.fillMaxSize(), factory = widgetFactory!!)
}

private fun createTerminalWidget(
    ttyConnectorFactory: () -> TtyConnector,
    terminalCloseCallback: () -> Unit
): JediTermWidget {
    val widget = JediTermWidget(80, 24, DefaultSettingsProvider())
    var ttyConnector = ttyConnectorFactory()
    widget.ttyConnector = ttyConnector
    thread {
        ttyConnector.waitFor()
        widget.close()
        terminalCloseCallback()
    }
    widget.start()
    return widget
}

fun createTtyConnector(): TtyConnector {
    return try {
        var envs = System.getenv()
        val command: Array<String>
        if (UIUtil.isWindows) {
            command = arrayOf("cmd.exe")
        } else {
            command = arrayOf("/bin/sh", "-c", "$(\$SHELL)", "--login")
            envs = HashMap(System.getenv())
            envs["TERM"] = "xterm-256color"
        }
        val process: PtyProcess = PtyProcessBuilder()
            .setCommand(command)
            .setEnvironment(envs).start()
        PtyProcessTtyConnector(process, StandardCharsets.UTF_8)
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}
