package com.project.nonclassicsets.data.files

import com.gaalex.mathcore.fuzzy.FisherFuzzyValue
import com.project.nonclassicsets.domain.model.EmptyFileException
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import com.project.nonclassicsets.domain.model.ImportPreview

object TxtQuantitiesParser {
    fun parse(lines: List<String>): ImportPreview {
        val rows = lines.filter { it.isNotBlank() }
        if (rows.isEmpty()) throw EmptyFileException()

        val valid = rows.mapNotNull { line ->
            FisherFuzzyValue.parseOrNull(line)?.parameters?.let {
                FuzzyQuantityDraft(name = null, k1 = it.k1, k2 = it.k2)
            }
        }

        return ImportPreview(
            valid = valid,
            skipped = rows.size - valid.size
        )
    }
}
