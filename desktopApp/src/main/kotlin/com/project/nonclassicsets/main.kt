package com.project.nonclassicsets

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.project.nonclassicsets.di.AppGraph
import dev.zacsweers.metro.createGraph
import java.awt.Dimension
import java.util.Locale

fun main() {
    Locale.setDefault(Locale.forLanguageTag("ru-RU"))
    val appGraph = createGraph<AppGraph>()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Fuzzy Lab",
            state = rememberWindowState(size = DpSize(1360.dp, 920.dp)),
        ) {
            window.minimumSize = Dimension(1180, 760)
            App(appGraph.metroViewModelFactory)
        }
    }
}
