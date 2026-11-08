package com.project.nonclassicsets.presentation.importdialog.viewmodel

sealed class ImportDialogEvent {
    data object Dismiss : ImportDialogEvent()
}
