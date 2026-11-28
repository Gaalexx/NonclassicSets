package com.project.nonclassicsets.presentation.calculator.state

import androidx.compose.runtime.Immutable
import com.gaalex.mathcore.fuzzy.AlphaLevelFuzzyValue
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.presentation.calculator.ui.FuzzyOperations
import com.project.nonclassicsets.presentation.shared.chart.ChartScale

@Immutable
data class CalculatorViewModelState(
    val firstQuantity: FuzzyQuantity? = null,
    val secondQuantity: FuzzyQuantity? = null,
    val operation: FuzzyOperations = FuzzyOperations.Idle,
    val result: AlphaLevelFuzzyValue? = null,
    val resultCurve: List<Double> = emptyList(),
    val resultScale: ChartScale? = null
)
