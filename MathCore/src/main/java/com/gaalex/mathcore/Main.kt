package com.gaalex.mathcore

import com.gaalex.mathcore.interval.Interval
import com.gaalex.mathcore.solver.BisectionSolver
import com.gaalex.mathcore.solver.SolverSettings

fun main() {
    println(
        BisectionSolver.solve(
            function = { x -> x * x - 2 },
            interval = Interval(-10.0, 0.0),
            settings = SolverSettings(xTolerance = 0.0001, maxIterations = 1000)
        )
    )
}
