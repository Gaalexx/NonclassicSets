package com.project.nonclassicsets.presentation.calculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaalex.mathcore.fuzzy.AlphaLevel
import com.gaalex.mathcore.fuzzy.AlphaLevelFuzzyValue
import com.gaalex.mathcore.fuzzy.uniformAlphas
import com.gaalex.mathcore.interval.Interval
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.usecases.MathUseCase
import com.project.nonclassicsets.presentation.calculator.state.CalculatorViewModelState
import com.project.nonclassicsets.presentation.calculator.ui.FuzzyOperations
import com.project.nonclassicsets.presentation.shared.chart.CURVE_POINTS
import com.project.nonclassicsets.presentation.shared.chart.chartScaleBetween
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class CalculatorViewModel(
    private val mathUseCase: MathUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CalculatorViewModelState())
    val state = _state.asStateFlow()

    fun onEvent(event: CalculatorViewModelCommands) {
        when (event) {
            is CalculatorViewModelCommands.ChangeParameters -> onChangeParameters(event)
            is CalculatorViewModelCommands.ChangeMembershipInput -> onChangeMembershipInput(event)
        }
    }

    private fun onChangeMembershipInput(event: CalculatorViewModelCommands.ChangeMembershipInput) {
        viewModelScope.launch {
            val value = event.input.toDoubleOrNull()
            if (value == null || value <= 0) {
                // TODO случай исключения
            } else {
                val res = mathUseCase.membership(_state.value.resultCurve, value)
                res.onSuccess {
                    _state.update {
                        it.copy(
                            membershipValueInput = event.input,
                            membershipValue = res.getOrNull()!!.toString()
                        )
                    }
                }

            }
        }
    }

    private fun onChangeParameters(event: CalculatorViewModelCommands.ChangeParameters) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    firstQuantity =
                        if (event.firstK1 != null && event.firstK2 != null) FuzzyQuantity(
                            id = "1",
                            name = "1",
                            k1 = event.firstK1,
                            k2 = event.firstK2
                        )
                        else null,
                    secondQuantity = if (event.secondK1 != null && event.secondK2 != null) FuzzyQuantity(
                        id = "2",
                        name = "2",
                        k1 = event.secondK1,
                        k2 = event.secondK2
                    )
                    else null,
                    operation = event.operation,
                    resultCurve = emptyList(),
                    resultScale = null
                )
            }

            if (_state.value.operation is FuzzyOperations.RealOperation && _state.value.firstQuantity != null && _state.value.secondQuantity != null) {
                val res = mathUseCase.operate(
                    _state.value.firstQuantity!!,
                    _state.value.secondQuantity!!,
                    _state.value.operation as FuzzyOperations.RealOperation
                )

                res.onSuccess { cuts ->
                    val bounds = cuts.firstOrNull() ?: return@onSuccess
                    val scale = chartScaleBetween(bounds.left, bounds.right)
                    if (scale.span <= 0.0 || !scale.span.isFinite()) return@onSuccess

                    val levels = uniformAlphas(cuts.size).zip(cuts) { alpha, (left, right) ->
                        AlphaLevel(alpha, Interval(left, right))
                    }
                    val result = AlphaLevelFuzzyValue(levels)
                    val curve = List(CURVE_POINTS) { index ->
                        result.membership(scale.xAt(index, CURVE_POINTS))
                    }

                    _state.update { state ->
                        if (state.operation == event.operation &&
                            state.firstQuantity?.k1 == event.firstK1 && state.firstQuantity?.k2 == event.firstK2 &&
                            state.secondQuantity?.k1 == event.secondK1 && state.secondQuantity?.k2 == event.secondK2
                        ) {
                            state.copy(resultCurve = curve, resultScale = scale)
                        } else state
                    }
                }

            }
        }
    }


}
