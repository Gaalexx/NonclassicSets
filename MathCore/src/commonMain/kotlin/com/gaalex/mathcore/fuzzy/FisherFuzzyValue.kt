package com.gaalex.mathcore.fuzzy

import com.gaalex.mathcore.interval.Interval
import com.gaalex.mathcore.solver.BisectionSolver
import com.gaalex.mathcore.solver.EquationSolver
import com.gaalex.mathcore.solver.SolverSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.math.exp
import kotlin.math.expm1
import kotlin.math.ln
import kotlin.math.ln1p


fun String.toFisherFuzzyValue(): FisherFuzzyValue {
    return FisherFuzzyValue.parse(this)
}

fun String.toFisherFuzzyValueOrNull(): FisherFuzzyValue? {
    return FisherFuzzyValue.parseOrNull(this)
}

class FisherFuzzyValue(
    val parameters: FisherParameters,
    private val solver: EquationSolver = BisectionSolver,
    private val settings: SolverSettings = solver.defaultSettings,
    private val maxBracketExpansions: Int = 100,
) : FuzzyValue {
    init {
        require(maxBracketExpansions > 0) { "maxBracketExpansions must be positive" }
    }

    override fun toString(): String {
        return "(Z Fisher)[${parameters.k1}; ${parameters.k2};]"
    }

    companion object {
        private val FISHER_REGEX = Regex(
            """\(\s*Z\s+Fisher\s*\)\s*\[\s*([^;\]]*?)\s*;\s*([^;\]]*?)\s*;?\s*]"""
        )

        fun parse(s: String): FisherFuzzyValue {
            val (k1Str, k2Str) = FISHER_REGEX.matchEntire(s.trim())?.destructured
                ?: throw FisherParseException.WrongFormat(s)

            val k1 = k1Str.toParameter("k1")
            val k2 = k2Str.toParameter("k2")

            val parameters = try {
                FisherParameters(k1 = k1, k2 = k2)
            } catch (e: IllegalArgumentException) {
                throw FisherParseException.InvalidParameters(k1, k2, e)
            } catch (e: ArithmeticException) {
                throw FisherParseException.InvalidParameters(k1, k2, e)
            }
            return FisherFuzzyValue(parameters = parameters)
        }

        fun parseOrNull(s: String): FisherFuzzyValue? {
            return try {
                parse(s)
            } catch (e: FisherParseException) {
                null
            }
        }

        private fun String.toParameter(name: String): Double {
            return replace(',', '.').toDoubleOrNull()
                ?: throw FisherParseException.NotANumber(name, this)
        }
    }

    override fun membership(x: Double): Double {
        require(x.isFinite()) { "x must be finite" }
        val mode = parameters.mode
        if (x < 0.0) return 0.0
        if (x == mode) return 1.0
        if (x == 0.0) return 0.0

        val logMembership = if (parameters.k1 == 2.0) {
            -(1.0 + parameters.k2 / 2.0) * ln1p(x)
        } else {
            (parameters.k1 / 2.0 - 1.0) * (ln(x) - ln(mode)) - (parameters.k1 / 2.0 + parameters.k2 / 2.0) * (ln1p(
                x
            ) - ln1p(mode))
        }
        if (logMembership.isNaN() || logMembership > 1e-12) {
            throw ArithmeticException("Cannot compute membership at x=$x")
        }
        return exp(logMembership.coerceAtMost(0.0))
    }

    private suspend fun rightBracket(alpha: Double): Double {
        var upper = maxOf(1.0, 2.0 * parameters.mode)
        if (!upper.isFinite() || upper <= parameters.mode) {
            throw ArithmeticException("Cannot represent the initial right bracket")
        }

        var expansions = 0
        while (membership(upper) > alpha) {
            currentCoroutineContext().ensureActive()
            if (expansions >= maxBracketExpansions) {
                throw ArithmeticException("Cannot bracket alpha=$alpha in $maxBracketExpansions expansions")
            }
            val next = upper * 2.0
            if (!next.isFinite() || next <= upper) {
                throw ArithmeticException("Right bracket overflow for alpha=$alpha")
            }
            upper = next
            expansions++
        }
        return upper
    }

    override suspend fun alphaCut(alpha: Double): Interval {
        require(alpha.isFinite() && alpha > 0.0 && alpha <= 1.0) {
            "alpha must be finite and in (0, 1]"
        }
        if (alpha == 1.0) return Interval(parameters.mode, parameters.mode)

        if (parameters.k1 == 2.0) {
            val right = expm1(-ln(alpha) / (1.0 + parameters.k2 / 2.0))
            if (!right.isFinite() || right <= 0.0) {
                throw ArithmeticException("Cannot represent the positive right boundary for alpha=$alpha")
            }
            return Interval(0.0, right)
        }

        return coroutineScope {
            val function = { x: Double ->
                coroutineContext.ensureActive()
                membership(x) - alpha
            }
            val left = async(Dispatchers.Default) {
                solver.solve(function, Interval(0.0, parameters.mode), settings)
            }
            val right = async(Dispatchers.Default) {
                val upper = rightBracket(alpha)
                solver.solve(function, Interval(parameters.mode, upper), settings)
            }

            Interval(left.await(), right.await())
        }
    }
}
