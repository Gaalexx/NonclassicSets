package com.project.nonclassicsets.domain.repository

import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.DatasetSource
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import kotlinx.coroutines.flow.StateFlow

interface FuzzySetRepository {
    val quantities: StateFlow<List<FuzzyQuantity>>
    val source: StateFlow<DatasetSource>
    val lastResult: StateFlow<ClusteringOutcome?>

    fun nextId(): String
    fun add(drafts: List<FuzzyQuantityDraft>, source: DatasetSource)
    fun replace(drafts: List<FuzzyQuantityDraft>, source: DatasetSource)
    fun remove(id: String)
    fun saveResult(result: ClusteringOutcome)
}
