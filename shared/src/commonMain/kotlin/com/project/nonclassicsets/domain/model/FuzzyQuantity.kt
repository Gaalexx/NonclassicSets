package com.project.nonclassicsets.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class FuzzyQuantity(
    val id: String,
    val name: String?,
    val k1: Double,
    val k2: Double
)

@Immutable
data class FuzzyQuantityDraft(
    val name: String?,
    val k1: Double,
    val k2: Double
)
