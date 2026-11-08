package com.project.nonclassicsets.presentation.workingset.state

import androidx.compose.runtime.Immutable
import com.project.nonclassicsets.domain.model.DatasetSource
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.QuantityWithCut
import com.project.nonclassicsets.presentation.shared.chart.ChartScale
import com.project.nonclassicsets.presentation.workingset.viewmodel.WorkingSetViewModelError

enum class StopModeType {
    Threshold, Count
}

@Immutable
data class WorkingSetViewModelState(
    val quantities: List<FuzzyQuantity> = emptyList(),
    val cuts: Map<String, QuantityWithCut> = emptyMap(),
    val source: DatasetSource = DatasetSource.Empty,
    val selectedId: String? = null,
    val selectedCurve: List<Double> = emptyList(),
    val selectedScale: ChartScale? = null,
    val alphaInput: String = "0,6",
    val alpha: Double? = 0.6,
    val stopModeType: StopModeType = StopModeType.Threshold,
    val epsilonInput: String = "0,05",
    val countInput: String = "3",
    val alphaError: Boolean = false,
    val epsilonError: Boolean = false,
    val countError: Boolean = false,
    val isCalculating: Boolean = false,
    val error: WorkingSetViewModelError = WorkingSetViewModelError.NoError
) {
    val selected: QuantityWithCut?
        get() = selectedId?.let { cuts[it] }
}
