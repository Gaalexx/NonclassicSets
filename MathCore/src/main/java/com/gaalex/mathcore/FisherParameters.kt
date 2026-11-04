package com.gaalex.mathcore

data class FisherParameters(
    val k1: Double,
    val k2: Double
) {
    init {
        require(k1.isFinite() && k1 >= 2.0) { "k1 must be finite and at least 2" }
        require(k2.isFinite() && k2 > 0.0) { "k2 must be finite and positive" }

        if (!mode.isFinite() || (k1 > 2.0 && mode <= 0.0)) {
            throw ArithmeticException("The positive mode cannot be represented as a finite Double")
        }
    }

    val mode: Double
        get() = (k1 - 2.0) / (k2 + 2.0)
}
