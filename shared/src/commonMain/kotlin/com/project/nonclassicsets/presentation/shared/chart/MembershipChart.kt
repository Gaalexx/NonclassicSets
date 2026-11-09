package com.project.nonclassicsets.presentation.shared.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.nonclassicsets.presentation.shared.formatDecimal
import com.project.nonclassicsets.presentation.theme.FuzzyLabTheme

val ChartPlotStart = 52.dp
val ChartPlotEnd = 24.dp
private val ChartPlotTop = 26.dp
private val ChartPlotBottom = 24.dp
private val ChartLabelGap = 6.dp

@Composable
fun MembershipChart(
    modifier: Modifier = Modifier,
    curves: List<ChartCurve>,
    scale: ChartScale,
    alpha: Double?,
    alphaLabel: String,
    muLabel: String,
    xLabel: String,
    curveWidth: Dp = 2.5.dp,
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val alphaStyle = labelStyle.copy(color = MaterialTheme.colorScheme.primary)
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val alphaColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        val plotLeft = ChartPlotStart.toPx()
        val plotTop = ChartPlotTop.toPx()
        val plotRight = size.width - ChartPlotEnd.toPx()
        val plotBottom = size.height - ChartPlotBottom.toPx()
        val plotWidth = plotRight - plotLeft
        val plotHeight = plotBottom - plotTop

        fun xToPx(x: Double) = plotLeft + ((x - scale.xMin) / scale.span * plotWidth).toFloat()
        fun yToPx(y: Double) = plotBottom - (y * plotHeight).toFloat()

        for (step in 0..5) {
            val mu = step / 5.0
            val y = yToPx(mu)
            drawLine(gridColor, Offset(plotLeft, y), Offset(plotRight, y), strokeWidth = 0.6.dp.toPx())
            drawLabel(
                textMeasurer, mu.formatDecimal(1, 1), labelStyle,
                Offset(plotLeft - ChartLabelGap.toPx(), y), alignEnd = true
            )
        }

        scale.ticks.forEach { tick ->
            val x = xToPx(tick)
            drawLine(gridColor, Offset(x, plotTop), Offset(x, plotBottom), strokeWidth = 0.45.dp.toPx())
            val layout = textMeasurer.measure(tick.formatDecimal(2), labelStyle)
            drawText(
                layout,
                topLeft = Offset(x - layout.size.width / 2f, plotBottom + ChartLabelGap.toPx())
            )
        }

        val muLayout = textMeasurer.measure(muLabel, labelStyle)
        drawText(muLayout, topLeft = Offset(0f, 0f))

        val xLayout = textMeasurer.measure(xLabel, labelStyle)
        drawText(
            xLayout,
            topLeft = Offset(plotRight + ChartLabelGap.toPx() * 2, plotBottom + ChartLabelGap.toPx())
        )

        curves.forEach { curve ->
            drawCurve(curve, ::xToPx, ::yToPx, scale, curveWidth.toPx())
        }

        if (alpha != null) {
            val y = yToPx(alpha)
            drawLine(
                color = alphaColor,
                start = Offset(plotLeft, y),
                end = Offset(plotRight, y),
                strokeWidth = 1.3.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))
            )
            val layout = textMeasurer.measure(alphaLabel, alphaStyle)
            drawText(
                layout,
                topLeft = Offset(
                    plotRight - layout.size.width - 60.dp.toPx(),
                    y - layout.size.height - 4.dp.toPx()
                )
            )
        }
    }
}

private fun DrawScope.drawCurve(
    curve: ChartCurve,
    xToPx: (Double) -> Float,
    yToPx: (Double) -> Float,
    scale: ChartScale,
    width: Float,
) {
    if (curve.values.size < 2) return
    val path = Path()
    curve.values.forEachIndexed { index, value ->
        val x = xToPx(scale.xAt(index, curve.values.size))
        val y = yToPx(value.coerceIn(0.0, 1.0))
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(
        path = path,
        color = curve.color,
        style = Stroke(
            width = if (curve.dashed) width * 0.8f else width,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = if (curve.dashed) {
                PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 3.dp.toPx()))
            } else {
                null
            }
        )
    )
}

private fun DrawScope.drawLabel(
    textMeasurer: TextMeasurer,
    text: String,
    style: TextStyle,
    anchor: Offset,
    alignEnd: Boolean,
) {
    val layout = textMeasurer.measure(text, style)
    val x = if (alignEnd) anchor.x - layout.size.width else anchor.x
    drawText(layout, topLeft = Offset(x, anchor.y - layout.size.height / 2f))
}

@Preview
@Composable
private fun MembershipChartPreview() {
    FuzzyLabTheme {
        MembershipChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            curves = listOf(
                ChartCurve(
                    values = List(CURVE_POINTS) { index ->
                        val x = 6.0 * index / (CURVE_POINTS - 1)
                        kotlin.math.exp(-(x - 1.0) * (x - 1.0))
                    },
                    color = Color(0xFF6750A4)
                )
            ),
            scale = ChartScale(xMax = 6.0, step = 1.0),
            alpha = 0.6,
            alphaLabel = "α = 0,6",
            muLabel = "μ(x)",
            xLabel = "x",
        )
    }
}
