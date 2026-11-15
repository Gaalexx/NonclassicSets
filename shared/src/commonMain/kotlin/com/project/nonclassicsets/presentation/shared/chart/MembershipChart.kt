package com.project.nonclassicsets.presentation.shared.chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.VicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.DashedShape
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.project.nonclassicsets.presentation.shared.formatDecimal

val ChartStartAxisWidth = 52.dp

@Composable
fun MembershipChart(
    modifier: Modifier = Modifier,
    curves: List<ChartCurve>,
    scale: ChartScale,
    alpha: Double?,
    alphaLabel: String,
    curveWidth: Dp = 2.5.dp,
    chartAreaHeight: Dp = 344.dp,
    scrollState: VicoScrollState = rememberVicoScrollState(scrollEnabled = false),
    zoomState: VicoZoomState = rememberVicoZoomState(zoomEnabled = false, initialZoom = Zoom.Content),
) {
    val model = remember(curves, scale) {
        CartesianChartModel(
            LineCartesianLayerModel.build {
                curves.forEach { curve ->
                    series(
                        x = List(curve.values.size) { scale.xAt(it, curve.values.size) },
                        y = curve.values
                    )
                }
            }
        )
    }
    val lines = remember(curves, curveWidth) {
        curves.map { curve ->
            LineCartesianLayer.Line(
                fill = LineCartesianLayer.LineFill.single(Fill(curve.color)),
                stroke = if (curve.dashed) {
                    LineCartesianLayer.LineStroke.Dashed(thickness = curveWidth, dashLength = 2.dp, gapLength = 3.dp)
                } else {
                    LineCartesianLayer.LineStroke.Continuous(thickness = curveWidth)
                }
            )
        }
    }
    val alphaLine = HorizontalLine(
        y = { alpha ?: 0.0 },
        line = rememberLineComponent(
            fill = Fill(MaterialTheme.colorScheme.primary),
            thickness = 1.3.dp,
            shape = DashedShape()
        ),
        labelComponent = rememberTextComponent(
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
        ),
        label = { alphaLabel }
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(lines),
                rangeProvider = CartesianLayerRangeProvider.fixed(
                    minX = scale.xMin,
                    maxX = scale.xMax,
                    minY = 0.0,
                    maxY = 1.0
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = CartesianValueFormatter { _, value, _ -> value.formatDecimal(1, 1) },
                itemPlacer = VerticalAxis.ItemPlacer.step({ 0.2 }),
                size = BaseAxis.Size.Fixed(ChartStartAxisWidth)
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = CartesianValueFormatter { _, value, _ -> value.formatDecimal(4) },
                itemPlacer = HorizontalAxis.ItemPlacer.aligned(shiftExtremeLabels = true)
            ),
            decorations = if (alpha != null) listOf(alphaLine) else emptyList(),
            getXStep = { _ -> scale.tickStep }
        ),
        model = model,
        modifier = modifier,
        scrollState = scrollState,
        zoomState = zoomState,
        chartAreaHeight = chartAreaHeight
    )
}
