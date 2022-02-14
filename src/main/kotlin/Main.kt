// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import api.AppListItem
import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.TtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.UIUtil
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.pty4j.PtyProcess
import com.pty4j.PtyProcessBuilder
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread
import kotlin.system.exitProcess

@Composable
fun App() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            val detailData = remember { mutableStateOf<Pair<String, AppListItem>?>(null) }
            val data = detailData.value
            if (data != null) {
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
//        App()
        Box(modifier = Modifier.fillMaxSize()) {
            SwingPanel(modifier = Modifier.fillMaxSize(), factory = {
                createTerminalWidget()
            })
        }
    }
}

private fun createTerminalWidget(): JediTermWidget {
    val widget = JediTermWidget(80, 24, DefaultSettingsProvider())
    val ttyConnector = createTtyConnector()
    ttyConnector.write("neofetch\n")
    widget.ttyConnector = ttyConnector
    widget.start()
    thread {
        ttyConnector.waitFor()
        widget.close()
        exitProcess(0)
    }
    return widget
}

private fun createTtyConnector(): TtyConnector {
    return try {
        var envs = System.getenv()
        val command: Array<String>
        if (UIUtil.isWindows) {
            command = arrayOf("cmd.exe")
        } else {
            command = arrayOf("/bin/bash", "--login")
            envs = HashMap(System.getenv())
            envs["TERM"] = "xterm-256color"
        }
        val process: PtyProcess = PtyProcessBuilder().setCommand(command).setEnvironment(envs).start()
        PtyProcessTtyConnector(process, StandardCharsets.UTF_8)
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}
