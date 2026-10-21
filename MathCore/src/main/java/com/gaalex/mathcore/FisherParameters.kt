package com.gaalex.mathcore

data class FisherParameters(
    val k1: Double,
    val k2: Double
) {
    init{
        require(k1 > 0 && k2 > 0) { "k1 and k2 must be positive" }
    }

    val mode: Double
        get() = (k1 - 2.0) / (k2 + 2)
}