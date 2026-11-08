package com.project.nonclassicsets.presentation.adddialog.viewmodel

sealed class AddQuantityDialogEvent {
    data object Dismiss : AddQuantityDialogEvent()
}
