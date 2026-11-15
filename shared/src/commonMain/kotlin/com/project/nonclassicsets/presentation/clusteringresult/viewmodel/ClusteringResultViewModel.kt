package com.project.nonclassicsets.presentation.clusteringresult.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.repository.FuzzyMathRepository
import com.project.nonclassicsets.domain.repository.FuzzySetRepository
import com.project.nonclassicsets.presentation.clusteringresult.state.ClusteringResultViewModelState
import com.project.nonclassicsets.presentation.clusteringresult.state.ResultCurve
import com.project.nonclassicsets.presentation.clusteringresult.state.ResultRow
import com.project.nonclassicsets.presentation.shared.chart.ZOOMABLE_CURVE_POINTS
import com.project.nonclassicsets.presentation.shared.chart.chartScaleBetween
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class ClusteringResultViewModel(
    private val setRepo: FuzzySetRepository,
    private val mathRepo: FuzzyMathRepository
) : ViewModel() {

    companion object {
        private const val MAX_DASHED_QUANTITIES = 12
        private const val CHART_X_MIN = -1.0
        private const val CHART_RIGHT_PADDING = 2.0
    }

    private val _state = MutableStateFlow(ClusteringResultViewModelState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            setRepo.lastResult.filterNotNull().collectLatest { outcome ->
                showOutcome(outcome)
            }
        }
    }

    private suspend fun showOutcome(outcome: ClusteringOutcome) {
        _state.update {
            it.copy(
                outcome = outcome,
                isLoading = true
            )
        }

        val withDashes = outcome.quantitiesCount <= MAX_DASHED_QUANTITIES
        val rows = outcome.clusters
            .flatMap { cluster ->
                cluster.members.mapIndexed { index, member ->
                    ResultRow(row = member, clusterNumber = cluster.number, dashed = withDashes && index > 0)
                }
            }
            .sortedWith(compareBy({ it.row.quantity.id.length }, { it.row.quantity.id }))

        val scale = chartScaleBetween(
            xMin = CHART_X_MIN,
            xMax = rows.maxOf { it.row.cut.right } + CHART_RIGHT_PADDING
        )

        val curves = coroutineScope {
            rows.map { row ->
                async {
                    ResultCurve(
                        clusterNumber = row.clusterNumber,
                        dashed = row.dashed,
                        values = mathRepo.membershipCurve(row.row.quantity, scale.xMin, scale.xMax, ZOOMABLE_CURVE_POINTS)
                    )
                }
            }.awaitAll()
        }

        _state.update {
            it.copy(
                rows = rows,
                curves = curves.sortedBy { curve -> curve.dashed },
                scale = scale,
                isLoading = false
            )
        }
    }
}
