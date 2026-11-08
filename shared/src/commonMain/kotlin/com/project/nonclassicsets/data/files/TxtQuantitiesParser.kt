package com.project.nonclassicsets.data.files

import com.gaalex.mathcore.fuzzy.FisherFuzzyValue
import com.gaalex.mathcore.fuzzy.FisherParseException
import com.project.nonclassicsets.domain.model.EmptyFileException
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import com.project.nonclassicsets.domain.model.ImportPreview
import com.project.nonclassicsets.domain.model.InvalidRow
import com.project.nonclassicsets.domain.model.InvalidRowReason

object TxtQuantitiesParser {
    fun parse(fileName: String, lines: List<String>): ImportPreview {
        val rows = lines.withIndex().filter { it.value.isNotBlank() }
        if (rows.isEmpty()) throw EmptyFileException()

        val valid = mutableListOf<FuzzyQuantityDraft>()
        val invalid = mutableListOf<InvalidRow>()

        rows.forEach { (index, line) ->
            try {
                val parameters = FisherFuzzyValue.parse(line).parameters
                valid.add(FuzzyQuantityDraft(name = null, k1 = parameters.k1, k2 = parameters.k2))
            } catch (e: FisherParseException) {
                invalid.add(InvalidRow(line = index + 1, reason = e.toReason()))
            }
        }

        return ImportPreview(
            fileName = fileName,
            valid = valid,
            invalid = invalid
        )
    }

    private fun FisherParseException.toReason(): InvalidRowReason {
        return when (this) {
            is FisherParseException.WrongFormat -> InvalidRowReason.WrongFormat
            is FisherParseException.NotANumber -> InvalidRowReason.NotANumber
            is FisherParseException.InvalidParameters -> when {
                !k1.isFinite() || !k2.isFinite() -> InvalidRowReason.NotANumber
                k1 < 2.0 -> InvalidRowReason.K1TooSmall
                k2 <= 0.0 -> InvalidRowReason.K2NotPositive
                else -> InvalidRowReason.NotANumber
            }
        }
    }
}
