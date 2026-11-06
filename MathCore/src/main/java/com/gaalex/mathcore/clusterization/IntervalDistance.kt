package com.gaalex.mathcore.clusterization

import com.gaalex.mathcore.interval.Interval

fun interface IntervalDistance {
    fun distance(a: Interval, b: Interval): Double
}