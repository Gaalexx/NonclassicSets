package com.gaalex.mathcore.fuzzy.operations

import com.gaalex.mathcore.fuzzy.FuzzyValue
import com.gaalex.mathcore.fuzzy.uniformAlphas

class FuzzyComparator(
    private val alphas: List<Double> = uniformAlphas()
) {
    suspend fun rank(value: FuzzyValue): Double {
        return alphas.map { alpha -> value.alphaCut(alpha).midpoint }.average()
    }

    suspend fun compare(a: FuzzyValue, b: FuzzyValue): Int {
        return rank(a).compareTo(rank(b))
    }
}
