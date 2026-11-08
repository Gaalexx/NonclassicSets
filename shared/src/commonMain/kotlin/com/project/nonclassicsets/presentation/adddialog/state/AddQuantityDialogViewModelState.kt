package com.project.nonclassicsets.presentation.adddialog.state

import androidx.compose.runtime.Immutable

@Immutable
data class AddQuantityDialogViewModelState(
    val nextId: String = "",
    val nameInput: String = "",
    val k1Input: String = "",
    val k2Input: String = "",
    val k1Error: Boolean = false,
    val k2Error: Boolean = false
)
