package com.project.nonclassicsets.presentation.generatedialog.viewmodel

sealed class GenerateDialogEvent {
    data object Dismiss : GenerateDialogEvent()
}
