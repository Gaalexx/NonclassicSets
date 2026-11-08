package com.project.nonclassicsets.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ClusterColor(
    val content: Color,
    val container: Color
)

private val ClusterPalette = listOf(
    ClusterColor(ClusterPurple, ClusterPurpleContainer),
    ClusterColor(ClusterTeal, ClusterTealContainer),
    ClusterColor(ClusterOrange, ClusterOrangeContainer),
    ClusterColor(ClusterBlue, ClusterBlueContainer),
    ClusterColor(ClusterPink, ClusterPinkContainer),
    ClusterColor(ClusterGreen, ClusterGreenContainer),
    ClusterColor(ClusterBrown, ClusterBrownContainer),
    ClusterColor(ClusterSlate, ClusterSlateContainer),
)

fun clusterColor(clusterNumber: Int): ClusterColor {
    return ClusterPalette[(clusterNumber - 1).mod(ClusterPalette.size)]
}
