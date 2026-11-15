package com.gaalex.mathcore.fuzzy

import com.gaalex.mathcore.interval.Interval

class AlphaLevelFuzzyValue(
    val levels: List<AlphaLevel>
) : FuzzyValue {
    init {
        require(levels.isNotEmpty()) { "levels must not be empty" }
        levels.zipWithNext { lower, upper ->
            require(lower.alpha < upper.alpha) { "alphas must be strictly increasing" }
            require(upper.cut.left in lower.cut && upper.cut.right in lower.cut) {
                "alpha-cuts must be nested"
            }
        }
    }

    override fun membership(x: Double): Double {
        val lowerIndex = levels.indexOfLast { x in it.cut }
        if (lowerIndex == -1) return 0.0

        val lower = levels[lowerIndex]
        val upper = levels.getOrNull(lowerIndex + 1) ?: return lower.alpha
        val fraction = if (x < upper.cut.left) {
            (x - lower.cut.left) / (upper.cut.left - lower.cut.left)
        } else {
            (lower.cut.right - x) / (lower.cut.right - upper.cut.right)
        }
        return interpolate(lower.alpha, upper.alpha, fraction)
    }

    override suspend fun alphaCut(alpha: Double): Interval {
        require(alpha >= levels.first().alpha && alpha <= levels.last().alpha) {
            "alpha must be in [${levels.first().alpha}, ${levels.last().alpha}]"
        }
        val upperIndex = levels.indexOfFirst { it.alpha >= alpha }
        val upper = levels[upperIndex]
        if (upper.alpha == alpha) return upper.cut

        val lower = levels[upperIndex - 1]
        val fraction = (alpha - lower.alpha) / (upper.alpha - lower.alpha)
        return Interval(
            left = interpolate(lower.cut.left, upper.cut.left, fraction),
            right = interpolate(lower.cut.right, upper.cut.right, fraction)
        )
    }

    private fun interpolate(from: Double, to: Double, fraction: Double): Double {
        return from + (to - from) * fraction
    }
}
