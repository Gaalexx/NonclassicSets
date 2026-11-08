package com.project.nonclassicsets.presentation.adddialog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nonclassicsets.domain.model.DatasetSource
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import com.project.nonclassicsets.domain.repository.FuzzySetRepository
import com.project.nonclassicsets.presentation.adddialog.state.AddQuantityDialogViewModelState
import com.project.nonclassicsets.presentation.shared.toDoubleInput
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class AddQuantityDialogViewModel(
    private val setRepo: FuzzySetRepository
) : ViewModel() {

    companion object {
        private const val DEFAULT_NAME = "Величина"
    }

    private val _state = MutableStateFlow(
        AddQuantityDialogViewModelState(
            nextId = setRepo.nextId(),
            nameInput = "$DEFAULT_NAME ${setRepo.nextId()}"
        )
    )
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddQuantityDialogEvent>()
    val event = _event.asSharedFlow()

    fun onEvent(event: AddQuantityDialogViewModelCommand) {
        when (event) {
            is AddQuantityDialogViewModelCommand.OnNameChange -> _state.update { it.copy(nameInput = event.value) }
            is AddQuantityDialogViewModelCommand.OnK1Change -> _state.update {
                it.copy(
                    k1Input = event.value,
                    k1Error = !isK1Valid(event.value)
                )
            }

            is AddQuantityDialogViewModelCommand.OnK2Change -> _state.update {
                it.copy(
                    k2Input = event.value,
                    k2Error = !isK2Valid(event.value)
                )
            }

            is AddQuantityDialogViewModelCommand.Add -> add()
        }
    }

    private fun add() {
        val current = _state.value
        val k1 = current.k1Input.toDoubleInput()?.takeIf { isK1Valid(current.k1Input) }
        val k2 = current.k2Input.toDoubleInput()?.takeIf { isK2Valid(current.k2Input) }

        if (k1 == null || k2 == null) {
            _state.update {
                it.copy(
                    k1Error = k1 == null,
                    k2Error = k2 == null
                )
            }
            return
        }

        setRepo.add(
            listOf(FuzzyQuantityDraft(name = current.nameInput, k1 = k1, k2 = k2)),
            DatasetSource.Manual
        )
        viewModelScope.launch {
            _event.emit(AddQuantityDialogEvent.Dismiss)
        }
    }

    private fun isK1Valid(value: String): Boolean = value.toDoubleInput()?.let { it >= 2.0 } ?: false

    private fun isK2Valid(value: String): Boolean = value.toDoubleInput()?.let { it > 0.0 } ?: false
}
