package com.project.nonclassicsets.presentation.importdialog.viewmodel

sealed class ImportDialogViewModelError {
    data object NoError : ImportDialogViewModelError()
    data object EmptyFile : ImportDialogViewModelError()
    data object ReadFailed : ImportDialogViewModelError()
}
