package com.gaalex.mathcore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.ln1p

interface FuzzyValue {
    val defaultEpsilon: Double
    val defaultMaxIterations: Int
    fun membership(x: Double): Double
    fun alphaCut(
        alpha: Double, epsilon: Double = defaultEpsilon, maxIterations: Int = defaultMaxIterations
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
                (parameters.k1 / 2.0 - 1.0) * (ln(x) - ln(parameters.mode)) - (parameters.k1 / 2.0 + parameters.k2 / 2.0) * (ln1p(
                    x
                ) - ln1p(parameters.mode))
            )
        }
    }

    private fun getBorder(
        alpha: Double,
        borderChange: (Double) -> Double = { it -> it * 2 },
        maxIteration: Int = defaultMaxIterations
    ): Double {
        var rightBorder: Double = maxOf(1.0, parameters.mode * 2.0)
        var iteration = 0
        while (membership(rightBorder) > alpha || iteration < maxIteration) {
            rightBorder = borderChange(rightBorder)
            iteration++
        }

        check(iteration < maxIteration) {
            "Could not have found right border for $maxIteration iterations"
        }
        return rightBorder
    }

    private suspend fun getIntervalOfAlpha(
        alpha: Double, epsilon: Double = defaultEpsilon, maxIterations: Int = defaultMaxIterations
    ): Interval? = supervisorScope {
        val f = { x: Double -> membership(x) - alpha }


        val left = async {
            BisectionSolver.solve(
                f, Interval(0.0, parameters.mode), tolerance = epsilon, maxIteration = maxIterations
            )
        }

        val right = async {
            BisectionSolver.solve(
                f, Interval(
                    parameters.mode, getBorder(alpha, maxIteration = maxIterations)
                ), tolerance = epsilon, maxIteration = maxIterations
            )
        }

        val leftB = left.await()
        val rightB = try {
            right.await()
        } catch (e: Exception) {
            return@supervisorScope null
        }
        return@supervisorScope Interval(leftB, rightB)
    }


    override fun alphaCut(alpha: Double, epsilon: Double, maxIterations: Int): Interval? {
        require(alpha > 0 && alpha <= 1)

        if (alpha in 1.0 - epsilon..1.0 + epsilon) {
            return Interval(parameters.mode, parameters.mode)
        }


        val scope = CoroutineScope(Dispatchers.Default)
        var answer: Interval? = null
        scope.launch {
            answer = getIntervalOfAlpha(
                alpha = alpha, epsilon = epsilon, maxIterations = maxIterations
            )
        }

        return answer
    }
}