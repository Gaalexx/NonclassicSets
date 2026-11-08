package com.project.nonclassicsets.domain.model

sealed class DatasetSource {
    data object Empty : DatasetSource()
    data object Manual : DatasetSource()
    data class Generated(val seed: Int) : DatasetSource()
    data class Imported(val fileName: String) : DatasetSource()
    data object Mixed : DatasetSource()
}
