package com.gaalex.mathcore

fun main(){
    println(
        BisectionSolver.solve(
            function = {x -> x * x - 2},
            interval = Interval(-10.0, 0.0),
            tolerance = 0.0001,
            maxIteration = 1000
        )
    )
}