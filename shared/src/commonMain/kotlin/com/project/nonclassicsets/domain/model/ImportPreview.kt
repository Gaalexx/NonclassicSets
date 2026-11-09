package com.project.nonclassicsets.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class ImportPreview(
    val valid: List<FuzzyQuantityDraft>,
    val skipped: Int
)

class EmptyFileException : Exception()
