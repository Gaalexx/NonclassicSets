package com.project.nonclassicsets.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class ImportPreview(
    val fileName: String,
    val valid: List<FuzzyQuantityDraft>,
    val invalid: List<InvalidRow>
)

@Immutable
data class InvalidRow(
    val line: Int,
    val reason: InvalidRowReason
)

sealed class InvalidRowReason {
    data object WrongFormat : InvalidRowReason()
    data object NotANumber : InvalidRowReason()
    data object K1TooSmall : InvalidRowReason()
    data object K2NotPositive : InvalidRowReason()
}

class EmptyFileException : Exception()
