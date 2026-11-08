package com.project.nonclassicsets.presentation.generatedialog.state

import androidx.compose.runtime.Immutable

@Immutable
data class GenerateDialogViewModelState(
    val countInput: String = "100",
    val k1FromInput: String = "2",
    val k1UntilInput: String = "30",
    val k2FromInput: String = "2",
    val k2UntilInput: String = "20",
    val seedInput: String = "42",
    val replace: Boolean = false,
    val countError: Boolean = false,
    val k1FromError: Boolean = false,
    val k1UntilError: Boolean = false,
    val k2FromError: Boolean = false,
    val k2UntilError: Boolean = false,
    val seedError: Boolean = false,
    val isGenerating: Boolean = false,
    val generationFailed: Boolean = false
) {
    val hasErrors: Boolean
        get() = countError || k1FromError || k1UntilError || k2FromError || k2UntilError || seedError
}
