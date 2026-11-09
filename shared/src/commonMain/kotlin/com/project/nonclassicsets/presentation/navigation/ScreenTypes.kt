package com.project.nonclassicsets.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenTypes : NavKey {
    @Serializable
    data object WorkingSet : ScreenTypes()

    @Serializable
    data object ClusteringResult : ScreenTypes()

    @Serializable
    data object GenerateQuantities : ScreenTypes()

    @Serializable
    data object AddQuantity : ScreenTypes()
}
