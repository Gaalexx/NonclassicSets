package com.project.nonclassicsets.presentation.clusteringresult.state

import androidx.compose.runtime.Immutable
import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.QuantityWithCut
import com.project.nonclassicsets.presentation.shared.chart.ChartScale

@Immutable
data class ResultRow(
    val row: QuantityWithCut,
    val clusterNumber: Int,
    val dashed: Boolean
)

@Immutable
data class ResultCurve(
    val clusterNumber: Int,
    val dashed: Boolean,
    val values: List<Double>
)

@Immutable
data class ClusteringResultViewModelState(
    val outcome: ClusteringOutcome? = null,
    val rows: List<ResultRow> = emptyList(),
    val curves: List<ResultCurve> = emptyList(),
    val scale: ChartScale? = null,
    val isLoading: Boolean = true
)
