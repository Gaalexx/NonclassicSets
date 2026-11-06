package com.gaalex.mathcore.clusterization

import com.gaalex.mathcore.interval.Interval

class AgglomerativeClusterer(
    private val distance: IntervalDistance
) : IntervalClusterer {

    override fun cluster(
        intervals: List<Interval>,
        mode: ClusterMode
    ): ClusteringResult {
        TODO()
    }
}