package com.project.nonclassicsets.presentation.importdialog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.nonclassicsets.domain.model.DatasetSource
import com.project.nonclassicsets.domain.model.EmptyFileException
import com.project.nonclassicsets.domain.repository.FilesRepository
import com.project.nonclassicsets.domain.repository.FuzzySetRepository
import com.project.nonclassicsets.presentation.importdialog.state.ImportDialogViewModelState
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
class ImportDialogViewModel(
    private val setRepo: FuzzySetRepository,
    private val filesRepo: FilesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ImportDialogViewModelState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ImportDialogEvent>()
    val event = _event.asSharedFlow()

    fun onEvent(event: ImportDialogViewModelCommand) {
        when (event) {
            is ImportDialogViewModelCommand.ChooseFile -> chooseFile()
            is ImportDialogViewModelCommand.Import -> import()
        }
    }

    private fun chooseFile() {
        viewModelScope.launch {
            val file = filesRepo.pickTxtFile() ?: return@launch

            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val result = filesRepo.readTxt(file)
            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        preview = result.getOrThrow(),
                        isLoading = false,
                        error = ImportDialogViewModelError.NoError
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        preview = null,
                        isLoading = false,
                        error = when (result.exceptionOrNull()) {
                            is EmptyFileException -> ImportDialogViewModelError.EmptyFile
                            else -> ImportDialogViewModelError.ReadFailed
                        }
                    )
                }
            }
        }
    }

    private fun import() {
        val preview = _state.value.preview ?: return
        if (preview.valid.isEmpty()) return

        setRepo.add(preview.valid, DatasetSource.Imported(preview.fileName))
        viewModelScope.launch {
            _event.emit(ImportDialogEvent.Dismiss)
        }
    }
}
