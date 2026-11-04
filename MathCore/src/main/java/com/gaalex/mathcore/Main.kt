package com.gaalex.mathcore

fun main() {
    println(
        BisectionSolver.solve(
            function = { x -> x * x - 2 },
            interval = Interval(-10.0, 0.0),
            settings = SolverSettings(xTolerance = 0.0001, maxIterations = 1000)
        )
    )
}
