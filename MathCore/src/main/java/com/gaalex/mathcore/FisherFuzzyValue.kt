package com.gaalex.mathcore

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.ln1p
class FisherFuzzyValue(
    val parameters: FisherParameters
) {
    fun membership(x: Double): Double {
        if(x.isNaN() || x.isInfinite()){
            throw ArithmeticException("x must be a number!")
        }

        return if(x <= 0){
            0.0
        }
        else{
            exp(
                (parameters.k1 / 2.0 - 1.0) * (ln(x) - ln(parameters.mode)) -
                        (parameters.k1 / 2.0 + parameters.k2 / 2.0) *
                        (ln1p(x) - ln1p(parameters.mode))
            )
        }
    }
}