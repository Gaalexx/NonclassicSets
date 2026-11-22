package com.project.nonclassicsets.presentation.calculator.viewmodel

import com.project.nonclassicsets.presentation.calculator.ui.FuzzyOperations

interface CalculatorViewModelCommands {
    data class ChangeParameters(
        val firstK1: Double? = null,
        val firstK2: Double? = null,
        val secondK1: Double? = null,
        val secondK2: Double? = null,
        val operation: FuzzyOperations = FuzzyOperations.Idle
    ) : CalculatorViewModelCommands
}
