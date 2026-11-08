package com.project.nonclassicsets.presentation.importdialog.state

import androidx.compose.runtime.Immutable
import com.project.nonclassicsets.domain.model.ImportPreview
import com.project.nonclassicsets.presentation.importdialog.viewmodel.ImportDialogViewModelError

@Immutable
data class ImportDialogViewModelState(
    val preview: ImportPreview? = null,
    val isLoading: Boolean = false,
    val error: ImportDialogViewModelError = ImportDialogViewModelError.NoError
)
