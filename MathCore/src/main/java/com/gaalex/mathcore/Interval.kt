package com.gaalex.mathcore

data class Interval(
    val left: Double,
    val right: Double
) {
    init {
        require(left.isFinite() && right.isFinite()) { "Interval boundaries must be finite" }
        require(left <= right) { "Left boundary must not exceed the right boundary" }
    }

    val width: Double
        get() = (right - left).also {
            if (!it.isFinite()) throw ArithmeticException("Interval width overflowed")
        }

    val midpoint: Double
        get() {
            val sum = left + right
            return if (sum.isFinite()) sum / 2.0 else left / 2.0 + right / 2.0
        }

    operator fun contains(x: Double): Boolean = x >= left && x <= right

    fun containsZero(): Boolean = 0.0 in this

    operator fun plus(other: Interval): Interval = arithmeticResult(
        left = left + other.left,
        right = right + other.right
    )

    operator fun minus(other: Interval): Interval = arithmeticResult(
        left = left - other.right,
        right = right - other.left
    )

    operator fun times(other: Interval): Interval {
        val products = doubleArrayOf(
            left * other.left,
            left * other.right,
            right * other.left,
            right * other.right
        )
        return arithmeticResult(products.min(), products.max())
    }

    operator fun div(other: Interval): Interval {
        if (other.containsZero()) {
            throw ArithmeticException("Cannot divide by an interval containing zero")
        }
        val quotients = doubleArrayOf(
            left / other.left,
            left / other.right,
            right / other.left,
            right / other.right
        )
        return arithmeticResult(quotients.min(), quotients.max())
    }

    private fun arithmeticResult(left: Double, right: Double): Interval {
        if (!left.isFinite() || !right.isFinite()) {
            throw ArithmeticException("Interval arithmetic produced a non-finite boundary")
        }
        return Interval(left, right)
    }
}
