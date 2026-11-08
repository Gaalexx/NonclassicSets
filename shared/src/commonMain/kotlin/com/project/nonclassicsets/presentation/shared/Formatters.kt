package com.project.nonclassicsets.presentation.shared

import com.project.nonclassicsets.domain.model.AlphaCut
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

fun Double.formatDecimal(maxDecimals: Int, minDecimals: Int = 0): String {
    val factor = 10.0.pow(maxDecimals).roundToLong()
    val rounded = (abs(this) * factor).roundToLong()
    val integerPart = rounded / factor
    var fraction = (rounded % factor).toString().padStart(maxDecimals, '0')

    while (fraction.length > minDecimals && fraction.endsWith('0')) {
        fraction = fraction.dropLast(1)
    }

    val sign = if (this < 0 && rounded != 0L) "-" else ""
    return if (fraction.isEmpty()) "$sign$integerPart" else "$sign$integerPart,$fraction"
}

fun Double.formatParameter(): String = formatDecimal(maxDecimals = 3, minDecimals = 1)

fun Double.formatLevel(): String = formatDecimal(maxDecimals = 4)

fun AlphaCut.format(): String {
    return "[${left.formatDecimal(3, 3)}; ${right.formatDecimal(3, 3)}]"
}

fun String.toDoubleInput(): Double? {
    return trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() }
}

fun String.toIntInput(): Int? {
    return trim().toIntOrNull()
}
