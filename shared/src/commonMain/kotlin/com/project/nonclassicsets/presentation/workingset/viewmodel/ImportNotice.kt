package com.project.nonclassicsets.presentation.workingset.viewmodel

sealed class ImportNotice {
    data object EmptyFile : ImportNotice()
    data object ReadFailed : ImportNotice()
    data class RowsSkipped(val skipped: Int, val added: Int) : ImportNotice()
}
