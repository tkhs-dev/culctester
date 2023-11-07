import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ui.MainScreen
import ui.MainScreenModel

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        MainScreen(MainScreenModel())
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "計算機テストツール v1.0.0") {
        App()
    }
}
