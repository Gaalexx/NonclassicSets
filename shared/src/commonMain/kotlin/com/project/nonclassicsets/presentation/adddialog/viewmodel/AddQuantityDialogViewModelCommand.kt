package com.project.nonclassicsets.presentation.adddialog.viewmodel

sealed class AddQuantityDialogViewModelCommand {
    data class OnNameChange(val value: String) : AddQuantityDialogViewModelCommand()
    data class OnK1Change(val value: String) : AddQuantityDialogViewModelCommand()
    data class OnK2Change(val value: String) : AddQuantityDialogViewModelCommand()
    data object Add : AddQuantityDialogViewModelCommand()
}
