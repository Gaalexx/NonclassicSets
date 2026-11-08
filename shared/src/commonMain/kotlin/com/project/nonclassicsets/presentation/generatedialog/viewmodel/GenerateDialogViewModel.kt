package com.project.nonclassicsets.presentation.generatedialog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nonclassicsets.domain.model.DatasetSource
import com.project.nonclassicsets.domain.model.GenerationSettings
import com.project.nonclassicsets.domain.repository.FuzzyMathRepository
import com.project.nonclassicsets.domain.repository.FuzzySetRepository
import com.project.nonclassicsets.presentation.generatedialog.state.GenerateDialogViewModelState
import com.project.nonclassicsets.presentation.shared.toDoubleInput
import com.project.nonclassicsets.presentation.shared.toIntInput
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
import kotlin.random.Random

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class GenerateDialogViewModel(
    private val setRepo: FuzzySetRepository,
    private val mathRepo: FuzzyMathRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GenerateDialogViewModelState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<GenerateDialogEvent>()
    val event = _event.asSharedFlow()

    fun onEvent(event: GenerateDialogViewModelCommand) {
        when (event) {
            is GenerateDialogViewModelCommand.OnCountChange -> updateInputs { it.copy(countInput = event.value) }
            is GenerateDialogViewModelCommand.OnK1FromChange -> updateInputs { it.copy(k1FromInput = event.value) }
            is GenerateDialogViewModelCommand.OnK1UntilChange -> updateInputs { it.copy(k1UntilInput = event.value) }
            is GenerateDialogViewModelCommand.OnK2FromChange -> updateInputs { it.copy(k2FromInput = event.value) }
            is GenerateDialogViewModelCommand.OnK2UntilChange -> updateInputs { it.copy(k2UntilInput = event.value) }
            is GenerateDialogViewModelCommand.OnSeedChange -> updateInputs { it.copy(seedInput = event.value) }
            is GenerateDialogViewModelCommand.OnReplaceChange -> _state.update { it.copy(replace = event.replace) }
            is GenerateDialogViewModelCommand.Generate -> generate()
        }
    }

    private fun updateInputs(change: (GenerateDialogViewModelState) -> GenerateDialogViewModelState) {
        _state.update { validate(change(it)) }
    }

    private fun validate(state: GenerateDialogViewModelState): GenerateDialogViewModelState {
        val k1From = state.k1FromInput.toDoubleInput()
        val k1Until = state.k1UntilInput.toDoubleInput()
        val k2From = state.k2FromInput.toDoubleInput()
        val k2Until = state.k2UntilInput.toDoubleInput()

        return state.copy(
            countError = state.countInput.toIntInput()?.let { it > 0 } != true,
            k1FromError = k1From == null || k1From < 2.0,
            k1UntilError = k1Until == null || (k1From != null && k1Until <= k1From),
            k2FromError = k2From == null || k2From <= 0.0,
            k2UntilError = k2Until == null || (k2From != null && k2Until <= k2From),
            seedError = state.seedInput.isNotBlank() && state.seedInput.toIntInput() == null,
            generationFailed = false
        )
    }

    private fun generate() {
        val current = validate(_state.value)
        _state.value = current
        if (current.hasErrors) return

        val seed = current.seedInput.toIntInput() ?: Random.nextInt()
        val settings = GenerationSettings(
            count = current.countInput.toIntInput()!!,
            k1From = current.k1FromInput.toDoubleInput()!!,
            k1Until = current.k1UntilInput.toDoubleInput()!!,
            k2From = current.k2FromInput.toDoubleInput()!!,
            k2Until = current.k2UntilInput.toDoubleInput()!!,
            seed = seed
        )

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isGenerating = true
                )
            }

            val result = mathRepo.generate(settings)
            if (result.isSuccess) {
                val drafts = result.getOrThrow()
                if (current.replace) {
                    setRepo.replace(drafts, DatasetSource.Generated(seed))
                } else {
                    setRepo.add(drafts, DatasetSource.Generated(seed))
                }
                _event.emit(GenerateDialogEvent.Dismiss)
            } else {
                _state.update {
                    it.copy(
                        isGenerating = false,
                        generationFailed = true
                    )
                }
            }
        }
    }
}
