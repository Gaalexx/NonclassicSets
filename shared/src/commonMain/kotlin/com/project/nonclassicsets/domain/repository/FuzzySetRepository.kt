package com.project.nonclassicsets.domain.repository

import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import kotlinx.coroutines.flow.StateFlow

interface FuzzySetRepository {
    val quantities: StateFlow<List<FuzzyQuantity>>
    val lastResult: StateFlow<ClusteringOutcome?>

    fun nextId(): String
    fun add(drafts: List<FuzzyQuantityDraft>)
    fun replace(drafts: List<FuzzyQuantityDraft>)
    fun remove(id: String)
    fun saveResult(result: ClusteringOutcome)
}
