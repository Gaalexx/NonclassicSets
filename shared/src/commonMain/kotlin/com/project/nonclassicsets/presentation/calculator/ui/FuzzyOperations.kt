package com.project.nonclassicsets.presentation.calculator.ui

sealed interface FuzzyOperations {
    object Idle : FuzzyOperations
    sealed interface RealOperation: FuzzyOperations{
        object Plus : RealOperation
        object Minus : RealOperation
        object Multiply : RealOperation
        object Divide : RealOperation
    }


}