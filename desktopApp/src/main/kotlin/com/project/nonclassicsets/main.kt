package com.project.nonclassicsets

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "NonclassicSets",
    ) {
        App()
    }
}