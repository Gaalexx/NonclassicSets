package com.gaalex.mathcore.clusterization

import com.gaalex.mathcore.interval.Interval
import kotlin.math.pow
import kotlin.math.sqrt

object EndpointIntervalDistance : IntervalDistance {
    override fun distance(
        a: Interval,
        b: Interval
    ): Double {
        return sqrt(((a.left - b.left).pow(2) + (a.right - b.right).pow(2)) / 2.0)
    }
}