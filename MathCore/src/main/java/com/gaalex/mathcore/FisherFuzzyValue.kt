package com.gaalex.mathcore

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.ln1p

interface FuzzyValue {
    val defaultEpsilon: Double
    val defaultMaxIterations: Int
    fun membership(x: Double): Double
    fun alphaCut(
        alpha: Double,
        epsilon: Double = defaultEpsilon,
        maxIterations: Int = defaultMaxIterations
    ): Interval?
}

class FisherFuzzyValue(
    val parameters: FisherParameters,
) : FuzzyValue {

    companion object {
        const val MAX_ITERATIONS = 100
        const val THIS_EPSILON = EPSILON
    }

    override val defaultEpsilon: Double
        get() = THIS_EPSILON


    override val defaultMaxIterations: Int
        get() = MAX_ITERATIONS

    override fun membership(x: Double): Double {
        if (x.isNaN() || x.isInfinite()) {
            throw ArithmeticException("x must be a number!")
        }

        return if (x <= 0) {
            0.0
        } else {
            exp(
                (parameters.k1 / 2.0 - 1.0) * (ln(x) - ln(parameters.mode)) -
                        (parameters.k1 / 2.0 + parameters.k2 / 2.0) *
                        (ln1p(x) - ln1p(parameters.mode))
            )
        }
    }

    private fun getRightBorder(alpha: Double, maxIterations: Int = defaultMaxIterations): Double {
        var rightBorder: Double = maxOf(1.0, parameters.mode * 2.0)
        var iteration = 0
        while (membership(rightBorder) > alpha || iteration < maxIterations) {
            rightBorder *= 2.0
            iteration++
        }

        check(iteration < maxIterations) {
            "Не удалось найти правую границу за $maxIterations итераций"
        }
        return rightBorder
    }

    override fun alphaCut(alpha: Double, epsilon: Double, maxIterations: Int): Interval? {
        require(alpha > 0 && alpha <= 1)

        if (alpha in 1.0 - epsilon..1.0 + epsilon) {
            return Interval(parameters.mode, parameters.mode)
        }

        val f = { x: Double -> membership(x) - alpha }

        val left = BisectionSolver.solve(
            f,
            Interval(0.0, parameters.mode), // TODO( при k2 < 2 левая граница будет другой )
            tolerance = epsilon,
            maxIteration = maxIterations
        )

        val right = try {
            BisectionSolver.solve(
                f,
                Interval(parameters.mode, getRightBorder(alpha, maxIterations)),
                tolerance = epsilon,
                maxIteration = maxIterations
            )
        } catch (e: Exception) {
            null
        }

        return if (right == null || left == null) {
            null
        } else {
            Interval(left, right)
        }
    }
}