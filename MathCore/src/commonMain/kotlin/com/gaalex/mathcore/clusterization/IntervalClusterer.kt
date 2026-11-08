package com.gaalex.mathcore.clusterization

import com.gaalex.mathcore.interval.Interval

interface IntervalClusterer {
    fun cluster(
        intervals: List<Interval>,
        mode: ClusterMode
    ): ClusteringResult
}