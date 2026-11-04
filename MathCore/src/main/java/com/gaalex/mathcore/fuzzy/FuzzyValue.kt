package com.gaalex.mathcore.fuzzy

import com.gaalex.mathcore.interval.Interval

interface FuzzyValue {
    fun membership(x: Double): Double
    suspend fun alphaCut(alpha: Double): Interval
}
