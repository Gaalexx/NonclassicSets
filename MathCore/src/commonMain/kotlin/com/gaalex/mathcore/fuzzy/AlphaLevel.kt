package com.gaalex.mathcore.fuzzy

import com.gaalex.mathcore.interval.Interval

data class AlphaLevel(
    val alpha: Double,
    val cut: Interval
) {
    init {
        require(alpha > 0.0 && alpha <= 1.0) { "alpha must be in (0, 1]" }
    }
}

fun uniformAlphas(count: Int = 100): List<Double> {
    require(count > 0) { "count must be positive" }
    return List(count) { index -> (index + 1.0) / count }
}
