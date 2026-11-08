package com.project.nonclassicsets.presentation.generatedialog.viewmodel

sealed class GenerateDialogViewModelCommand {
    data class OnCountChange(val value: String) : GenerateDialogViewModelCommand()
    data class OnK1FromChange(val value: String) : GenerateDialogViewModelCommand()
    data class OnK1UntilChange(val value: String) : GenerateDialogViewModelCommand()
    data class OnK2FromChange(val value: String) : GenerateDialogViewModelCommand()
    data class OnK2UntilChange(val value: String) : GenerateDialogViewModelCommand()
    data class OnSeedChange(val value: String) : GenerateDialogViewModelCommand()
    data class OnReplaceChange(val replace: Boolean) : GenerateDialogViewModelCommand()
    data object Generate : GenerateDialogViewModelCommand()
}
