package com.project.nonclassicsets.presentation.workingset.viewmodel

sealed class WorkingSetEvent {
    data object NavigateToResult : WorkingSetEvent()
}
