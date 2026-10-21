package com.gaalex.mathcore

data class Interval(
    val left: Double,
    val right: Double
){
    init{
        require(left <= right) { "Left border must be less or equal than right one" }
        require(!left.isNaN() && !right.isNaN()) { "Border must not be NaN" }
    }

    val width: Double
        get() = right - left
    val midpoint: Double
        get() = (left + right) / 2.0

    fun contains(x: Double): Boolean = x in left..right

    fun containsZero(): Boolean = contains(0.0)

    operator fun plus(other: Interval): Interval = Interval(
        left = left + other.left,
        right = right + other.right
    )

    operator fun minus(other: Interval): Interval = Interval(
        left = left - other.left,
        right = right - other.right
    )

    operator fun times(other: Interval): Interval {
        val combinations = arrayOf(left * other.left, left * other.right, right * other.left, right * other.right)
        return Interval(left = combinations.min(), right = combinations.max())
    }

    operator fun div(other: Interval): Interval {
        if(other.containsZero()){
            throw ArithmeticException("Divisor contains zero!")
        }
        val combinations = arrayOf(left / other.left, left / other.right, right / other.left, right / other.right)
        return Interval(left = combinations.min(), right = combinations.max())
    }

}