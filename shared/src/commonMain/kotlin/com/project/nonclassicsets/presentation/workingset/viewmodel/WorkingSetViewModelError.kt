package com.project.nonclassicsets.presentation.workingset.viewmodel

sealed class WorkingSetViewModelError {
    data object NoError : WorkingSetViewModelError()
    data object CutsFailed : WorkingSetViewModelError()
    data object ClusteringFailed : WorkingSetViewModelError()
}
