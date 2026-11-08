package com.project.nonclassicsets.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AlphaCut(
    val left: Double,
    val right: Double
)

@Immutable
data class QuantityWithCut(
    val quantity: FuzzyQuantity,
    val mode: Double,
    val cut: AlphaCut
)
