import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*
import logic.Formula
import logic.Formula.*
import logic.Generator
import org.apache.commons.exec.CommandLine
import java.io.File
import java.io.FileFilter
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = File("test").absolutePath
        }) {
            Text(text)
        }
    }
}

//fun main() = application {
//    Window(onCloseRequest = ::exitApplication) {
//        App()
//    }
//}

fun main() {
//    print("generating...\n")
//    val  startTime = System.currentTimeMillis()
//    runBlocking {
//        (0..100).map {
//            async {Generator().generate(1,100)}
//        }.awaitAll()
//    }.forEach(::println)
//    val  endTime = System.currentTimeMillis()
//    print("----GENERATED----\n")
//    print("time:${endTime-startTime}ms\n")
//
    val files = File("test").also { if(!it.exists()) it.mkdir() }
        .listFiles { it -> it?.name?.endsWith(".exe") ?: false }
    print(files.first().absolutePath)
    val cmd = CommandLine(files.first().absolutePath)
}
