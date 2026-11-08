package com.gaalex.mathcore.solver

data class SolverSettings(
    val xTolerance: Double = 1e-10, val maxIterations: Int = 200
) {
    init {
        require(xTolerance.isFinite() && xTolerance > 0.0) {
            "xTolerance must be positive and finite"
        }
        require(maxIterations > 0) { "maxIterations must be positive" }
    }
}
