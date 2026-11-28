package com.project.nonclassicsets.domain.repository

import com.gaalex.mathcore.fuzzy.AlphaLevelFuzzyValue
import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import com.project.nonclassicsets.domain.model.GenerationSettings
import com.project.nonclassicsets.domain.model.QuantityWithCut
import com.project.nonclassicsets.domain.model.StopMode

interface FuzzyMathRepository {
    suspend fun alphaCuts(
        quantities: List<FuzzyQuantity>,
        alpha: Double
    ): Result<List<QuantityWithCut>>

    suspend fun membershipCurve(
        quantity: FuzzyQuantity,
        xMin: Double,
        xMax: Double,
        pointsCount: Int
    ): List<Double>

    suspend fun cluster(
        rows: List<QuantityWithCut>,
        alpha: Double,
        stopMode: StopMode
    ): Result<ClusteringOutcome>

    suspend fun generate(settings: GenerationSettings): Result<List<FuzzyQuantityDraft>>
    suspend fun add(first: FuzzyQuantity, second: FuzzyQuantity): Result<AlphaLevelFuzzyValue>
    suspend fun subtract(first: FuzzyQuantity, second: FuzzyQuantity): Result<AlphaLevelFuzzyValue>
    suspend fun multiply(first: FuzzyQuantity, second: FuzzyQuantity): Result<AlphaLevelFuzzyValue>
    suspend fun divide(first: FuzzyQuantity, second: FuzzyQuantity): Result<AlphaLevelFuzzyValue>
}

