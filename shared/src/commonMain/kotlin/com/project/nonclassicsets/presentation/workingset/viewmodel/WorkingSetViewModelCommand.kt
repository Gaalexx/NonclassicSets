package com.project.nonclassicsets.presentation.workingset.viewmodel

import com.project.nonclassicsets.presentation.workingset.state.StopModeType

sealed class WorkingSetViewModelCommand {
    data class SelectQuantity(val id: String) : WorkingSetViewModelCommand()
    data class DeleteQuantity(val id: String) : WorkingSetViewModelCommand()
    data class OnAlphaChange(val value: String) : WorkingSetViewModelCommand()
    data class OnStopModeChange(val mode: StopModeType) : WorkingSetViewModelCommand()
    data class OnEpsilonChange(val value: String) : WorkingSetViewModelCommand()
    data class OnCountChange(val value: String) : WorkingSetViewModelCommand()
    data object Cluster : WorkingSetViewModelCommand()
}
