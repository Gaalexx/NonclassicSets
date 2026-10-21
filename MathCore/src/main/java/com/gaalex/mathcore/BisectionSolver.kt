package com.gaalex.mathcore

import kotlin.math.abs

object BisectionSolver {
    private fun equalsToZero(
        value: Double,
        tolerance: Double
    ): Boolean{
        return value in -abs(tolerance)..abs(tolerance)
    }

    private fun haveDifferentSigns(
        first: Double,
        second: Double
    ): Boolean{
        return !((first < 0 && second < 0) || (first > 0 && second > 0))
    }

    private fun binarySearch(
        function: (Double) -> Double,
        interval: Interval,
        tolerance: Double,
        maxIteration: Int,
    ): Double{
        var left = interval.left
        var right = interval.right
        var mid = (left + right) / 2.0
        var midValue = function(mid)
        var iterations = 0

        while(!equalsToZero(midValue, tolerance) && maxIteration >= iterations++){
            mid = (left + right) / 2.0
            midValue = function(mid)
            if(haveDifferentSigns(function(left), midValue)){
                right = mid
            }
            else{
                left = mid
            }
        }

        if(maxIteration < iterations){
            throw ArithmeticException("Out of iterations")
        }
        else{
            return mid
        }
    }

    fun solve(
        function: (Double) -> Double,
        interval: Interval,
        tolerance: Double = 1e-10,
        maxIteration: Int = 200
    ): Double{
        val borderValues = function(interval.left) to function(interval.right)
        if(equalsToZero(borderValues.first, tolerance)){
            return borderValues.first
        }
        if(equalsToZero(borderValues.second, tolerance)){
            return borderValues.second
        }

        if(!haveDifferentSigns(borderValues.first, borderValues.second)){
            throw ArithmeticException("Function does not have any solutions on that interval")
        }

        return binarySearch(
            function = function,
            interval = interval,
            tolerance = tolerance,
            maxIteration = maxIteration
        )
    }
}