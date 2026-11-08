package com.gaalex.mathcore.clusterization

sealed interface ClusterMode {
    data class ByThreshold(val epsilon: Double) : ClusterMode
    data class ByCount(val count: Int) : ClusterMode
}