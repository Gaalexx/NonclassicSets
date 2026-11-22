package com.project.nonclassicsets.presentation.workingset.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nonclassicsets.domain.model.EmptyFileException
import com.project.nonclassicsets.domain.model.StopMode
import com.project.nonclassicsets.domain.repository.FilesRepository
import com.project.nonclassicsets.domain.repository.FuzzyMathRepository
import com.project.nonclassicsets.domain.repository.FuzzySetRepository
import com.project.nonclassicsets.presentation.shared.chart.CURVE_POINTS
import com.project.nonclassicsets.presentation.shared.chart.SINGLE_CURVE_TAIL
import com.project.nonclassicsets.presentation.shared.chart.chartScaleFor
import com.project.nonclassicsets.presentation.shared.toDoubleInput
import com.project.nonclassicsets.presentation.shared.toIntInput
import com.project.nonclassicsets.presentation.workingset.state.StopModeType
import com.project.nonclassicsets.presentation.workingset.state.WorkingSetViewModelState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class WorkingSetViewModel(
    private val setRepo: FuzzySetRepository,
    private val mathRepo: FuzzyMathRepository,
    private val filesRepo: FilesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WorkingSetViewModelState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<WorkingSetEvent>()
    val event = _event.asSharedFlow()

    private val alpha = MutableStateFlow<Double?>(_state.value.alpha)
    private var curveJob: Job? = null

    init {
        viewModelScope.launch {
            setRepo.quantities.collect { quantities ->
                _state.update {
                    it.copy(
                        quantities = quantities,
                        selectedId = it.selectedId?.takeIf { id -> quantities.any { q -> q.id == id } }
                            ?: quantities.firstOrNull()?.id,
                        countError = it.stopModeType == StopModeType.Count &&
                                !isCountValid(it.countInput, quantities.size)
                    )
                }
            }
        }

        viewModelScope.launch {
            combine(setRepo.quantities, alpha.filterNotNull()) { quantities, alpha ->
                quantities to alpha
            }.collectLatest { (quantities, alpha) ->
                val result = mathRepo.alphaCuts(quantities, alpha)
                if (result.isSuccess) {
                    _state.update {
                        it.copy(
                            cuts = result.getOrThrow().associateBy { row -> row.quantity.id },
                            error = WorkingSetViewModelError.NoError
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            cuts = emptyMap(),
                            error = WorkingSetViewModelError.CutsFailed
                        )
                    }
                }
                updateSelectedCurve()
            }
        }
    }

    fun onEvent(event: WorkingSetViewModelCommand) {
        when (event) {
            is WorkingSetViewModelCommand.SelectQuantity -> selectQuantity(event.id)
            is WorkingSetViewModelCommand.DeleteQuantity -> setRepo.remove(event.id)
            is WorkingSetViewModelCommand.OnAlphaChange -> alphaChanged(event.value)
            is WorkingSetViewModelCommand.OnStopModeChange -> stopModeChanged(event.mode)
            is WorkingSetViewModelCommand.OnEpsilonChange -> epsilonChanged(event.value)
            is WorkingSetViewModelCommand.OnCountChange -> countChanged(event.value)
            is WorkingSetViewModelCommand.Cluster -> cluster()
            is WorkingSetViewModelCommand.ImportFile -> importFile()
            is WorkingSetViewModelCommand.DismissImportNotice -> _state.update {
                it.copy(
                    importNotice = null
                )
            }

            is WorkingSetViewModelCommand.ClearQuantities -> clearQuantities()
        }
    }

    private fun clearQuantities() {
        viewModelScope.launch {
            _state.update { it.copy(quantities = emptyList(), cuts = emptyMap()) }
            setRepo.clear()
        }
    }

    private fun importFile() {
        if (_state.value.isImporting) return

        viewModelScope.launch {
            _state.update { it.copy(isImporting = true) }
            val file = filesRepo.pickTxtFile()
            if (file == null) {
                _state.update { it.copy(isImporting = false) }
                return@launch
            }

            val result = filesRepo.readTxt(file)
            val preview = result.getOrNull()
            if (preview != null) {
                setRepo.add(preview.valid)
            }
            _state.update {
                it.copy(
                    isImporting = false,
                    importNotice = when {
                        preview == null && result.exceptionOrNull() is EmptyFileException -> ImportNotice.EmptyFile
                        preview == null -> ImportNotice.ReadFailed
                        preview.skipped > 0 -> ImportNotice.RowsSkipped(
                            skipped = preview.skipped,
                            added = preview.valid.size
                        )

                        else -> null
                    }
                )
            }
        }
    }

    private fun selectQuantity(id: String) {
        _state.update {
            it.copy(
                selectedId = id
            )
        }
        updateSelectedCurve()
    }

    private fun alphaChanged(value: String) {
        val parsed = value.toDoubleInput()?.takeIf { it > 0.0 && it <= 1.0 }
        _state.update {
            it.copy(
                alphaInput = value,
                alpha = parsed ?: it.alpha,
                alphaError = parsed == null
            )
        }
        if (parsed != null) {
            alpha.value = parsed
        }
    }

    private fun stopModeChanged(mode: StopModeType) {
        _state.update {
            it.copy(
                stopModeType = mode
            )
        }
    }

    private fun epsilonChanged(value: String) {
        _state.update {
            it.copy(
                epsilonInput = value,
                epsilonError = !isEpsilonValid(value)
            )
        }
    }

    private fun countChanged(value: String) {
        _state.update {
            it.copy(
                countInput = value,
                countError = !isCountValid(value, it.quantities.size)
            )
        }
    }

    private fun updateSelectedCurve() {
        curveJob?.cancel()
        curveJob = viewModelScope.launch {
            val selected = _state.value.selected
            if (selected == null) {
                _state.update {
                    it.copy(
                        selectedCurve = emptyList(),
                        selectedScale = null
                    )
                }
                return@launch
            }

            val scale = chartScaleFor(maxOf(selected.cut.right, selected.mode), SINGLE_CURVE_TAIL)
            val curve =
                mathRepo.membershipCurve(selected.quantity, scale.xMin, scale.xMax, CURVE_POINTS)
            _state.update {
                it.copy(
                    selectedCurve = curve,
                    selectedScale = scale
                )
            }
        }
    }

    private fun cluster() {
        val current = _state.value
        val alphaValue = current.alphaInput.toDoubleInput()?.takeIf { it > 0.0 && it <= 1.0 }
        val stopMode = when (current.stopModeType) {
            StopModeType.Threshold -> current.epsilonInput.toDoubleInput()
                ?.takeIf { it >= 0.0 }
                ?.let { StopMode.ByThreshold(it) }

            StopModeType.Count -> current.countInput.toIntInput()
                ?.takeIf { it in 1..current.quantities.size }
                ?.let { StopMode.ByCount(it) }
        }

        if (alphaValue == null || stopMode == null) {
            _state.update {
                it.copy(
                    alphaError = alphaValue == null,
                    epsilonError = it.stopModeType == StopModeType.Threshold && stopMode == null,
                    countError = it.stopModeType == StopModeType.Count && stopMode == null
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isCalculating = true
                )
            }

            val rows = mathRepo.alphaCuts(current.quantities, alphaValue)
                .getOrNull()
            val result = rows?.let { mathRepo.cluster(it, alphaValue, stopMode) }

            if (result != null && result.isSuccess) {
                setRepo.saveResult(result.getOrThrow())
                _state.update {
                    it.copy(
                        isCalculating = false,
                        error = WorkingSetViewModelError.NoError
                    )
                }
                _event.emit(WorkingSetEvent.NavigateToResult)
            } else {
                _state.update {
                    it.copy(
                        isCalculating = false,
                        error = WorkingSetViewModelError.ClusteringFailed
                    )
                }
            }
        }
    }

    private fun isEpsilonValid(value: String): Boolean {
        return value.toDoubleInput()?.let { it >= 0.0 } ?: false
    }

    private fun isCountValid(value: String, size: Int): Boolean {
        if (size == 0) return true
        return value.toIntInput()?.let { it in 1..size } ?: false
    }
}
