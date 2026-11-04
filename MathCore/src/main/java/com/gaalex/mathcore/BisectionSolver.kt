package com.gaalex.mathcore

object BisectionSolver : EquationSolver {
    override val defaultSettings: SolverSettings = SolverSettings()

    override fun solve(
        function: (Double) -> Double,
        interval: Interval,
        settings: SolverSettings
    ): Double {
        var left = interval.left
        var right = interval.right
        var leftValue = finiteValue(function, left)
        val rightValue = finiteValue(function, right)

        if (leftValue == 0.0) return left
        if (rightValue == 0.0) return right
        require((leftValue < 0.0) != (rightValue < 0.0)) {
            "Function values at the interval boundaries must have opposite signs"
        }

        repeat(settings.maxIterations) {
            if (right - left <= settings.xTolerance) return Interval(left, right).midpoint

            val midpoint = Interval(left, right).midpoint
            if (midpoint == left || midpoint == right) {
                throw ArithmeticException("Bisection cannot reach xTolerance at Double precision")
            }
            val midpointValue = finiteValue(function, midpoint)
            if (midpointValue == 0.0) return midpoint

            if ((leftValue < 0.0) != (midpointValue < 0.0)) {
                right = midpoint
            } else {
                left = midpoint
                leftValue = midpointValue
            }
        }

        if (right - left <= settings.xTolerance) return Interval(left, right).midpoint
        throw ArithmeticException("Bisection did not converge in ${settings.maxIterations} iterations")
    }

    private fun finiteValue(function: (Double) -> Double, x: Double): Double {
        val value = function(x)
        if (!value.isFinite()) {
            throw ArithmeticException("Function returned a non-finite value at x=$x")
        }
        return value
    }
}
