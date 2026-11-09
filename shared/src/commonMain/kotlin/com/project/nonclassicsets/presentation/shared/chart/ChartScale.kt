package com.project.nonclassicsets.presentation.shared.chart

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

@Immutable
data class ChartScale(
    val xMax: Double,
    val step: Double,
    val xMin: Double = 0.0
) {
    val span: Double
        get() = xMax - xMin

    val ticks: List<Double>
        get() {
            val first = ceil(xMin / step - TICK_EPSILON).toInt()
            val last = floor(xMax / step + TICK_EPSILON).toInt()
            return (first..last).map { it * step }
        }

    fun xAt(index: Int, pointsCount: Int): Double = xMin + span * index / (pointsCount - 1)
}

@Immutable
data class ChartCurve(
    val values: List<Double>,
    val color: Color,
    val dashed: Boolean = false
)

const val CURVE_POINTS = 240

private const val TICKS_COUNT = 6
private const val TICK_EPSILON = 1e-9
const val SINGLE_CURVE_TAIL = 4.0
const val MANY_CURVES_TAIL = 1.6

fun chartScaleFor(maxRight: Double, tail: Double = MANY_CURVES_TAIL): ChartScale {
    val raw = (maxRight * tail).coerceAtLeast(0.5)
    val step = niceStep(raw / TICKS_COUNT)
    return ChartScale(xMax = ceil(raw / step) * step, step = step)
}

fun chartScaleBetween(xMin: Double, xMax: Double): ChartScale {
    val step = niceStep((xMax - xMin) / TICKS_COUNT)
    return ChartScale(xMax = xMax, step = step, xMin = xMin)
}

private fun niceStep(value: Double): Double {
    val magnitude = 10.0.pow(floor(log10(value)))
    val normalized = value / magnitude
    val nice = when {
        normalized <= 1.0 -> 1.0
        normalized <= 2.0 -> 2.0
        normalized <= 2.5 -> 2.5
        normalized <= 5.0 -> 5.0
        else -> 10.0
    }
    return nice * magnitude
}
