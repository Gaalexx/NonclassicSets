package com.project.nonclassicsets.domain.model

import androidx.compose.runtime.Immutable

sealed class StopMode {
    data class ByThreshold(val epsilon: Double) : StopMode()
    data class ByCount(val count: Int) : StopMode()
}

@Immutable
data class Cluster(
    val number: Int,
    val members: List<QuantityWithCut>
)

@Immutable
data class ClusteringOutcome(
    val alpha: Double,
    val stopMode: StopMode,
    val clusters: List<Cluster>
) {
    val quantitiesCount: Int
        get() = clusters.sumOf { it.members.size }
}
