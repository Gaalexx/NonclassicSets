package com.gaalex.mathcore.fuzzy.operations

import com.gaalex.mathcore.fuzzy.AlphaLevel
import com.gaalex.mathcore.fuzzy.AlphaLevelFuzzyValue
import com.gaalex.mathcore.fuzzy.FuzzyValue
import com.gaalex.mathcore.fuzzy.uniformAlphas
import com.gaalex.mathcore.interval.Interval

class FuzzyArithmetic(
    private val alphas: List<Double> = uniformAlphas()
) {
    suspend fun add(a: FuzzyValue, b: FuzzyValue): AlphaLevelFuzzyValue {
        return combine(a, b, Interval::plus)
    }

    suspend fun subtract(a: FuzzyValue, b: FuzzyValue): AlphaLevelFuzzyValue {
        return combine(a, b, Interval::minus)
    }

    suspend fun multiply(a: FuzzyValue, b: FuzzyValue): AlphaLevelFuzzyValue {
        return combine(a, b, Interval::times)
    }

    suspend fun divide(a: FuzzyValue, b: FuzzyValue): AlphaLevelFuzzyValue {
        return combine(a, b, Interval::div)
    }

    private suspend fun combine(
        a: FuzzyValue,
        b: FuzzyValue,
        operation: (Interval, Interval) -> Interval
    ): AlphaLevelFuzzyValue {
        val levels = alphas.map { alpha ->
            AlphaLevel(alpha, operation(a.alphaCut(alpha), b.alphaCut(alpha)))
        }
        return AlphaLevelFuzzyValue(levels)
    }
}
