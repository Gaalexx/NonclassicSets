package com.project.nonclassicsets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.project.nonclassicsets.presentation.navigation.AppNavigation
import com.project.nonclassicsets.presentation.theme.FuzzyLabTheme
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@Composable
fun App(viewModelFactory: MetroViewModelFactory) {
    CompositionLocalProvider(LocalMetroViewModelFactory provides viewModelFactory) {
        FuzzyLabTheme {
            AppNavigation()
        }
    }
}
