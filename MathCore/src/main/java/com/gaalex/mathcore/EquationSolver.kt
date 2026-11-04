package com.gaalex.mathcore

interface EquationSolver {
    val defaultSettings: SolverSettings
    fun solve(
        function: (Double) -> Double,
        interval: Interval,
        settings: SolverSettings = defaultSettings
    ): Double
}
