package com.project.nonclassicsets.presentation.importdialog.viewmodel

sealed class ImportDialogViewModelCommand {
    data object ChooseFile : ImportDialogViewModelCommand()
    data object Import : ImportDialogViewModelCommand()
}
