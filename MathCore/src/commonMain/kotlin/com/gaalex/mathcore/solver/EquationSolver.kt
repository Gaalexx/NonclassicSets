package com.gaalex.mathcore.solver

import com.gaalex.mathcore.interval.Interval

interface EquationSolver {
    val defaultSettings: SolverSettings
    fun solve(
        function: (Double) -> Double,
        interval: Interval,
        settings: SolverSettings = defaultSettings
    ): Double
}
