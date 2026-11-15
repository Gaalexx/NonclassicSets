package com.project.nonclassicsets.presentation.shared.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.VicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.project.nonclassicsets.domain.model.AlphaCut
import com.project.nonclassicsets.presentation.shared.format
import kotlin.math.roundToInt

val AlphaCutsBoundsWidth = 124.dp

@Immutable
data class AlphaCutBar(
    val label: String,
    val cut: AlphaCut,
    val color: Color
)

@Composable
fun AlphaCutsChart(
    modifier: Modifier = Modifier,
    bars: List<AlphaCutBar>,
    scale: ChartScale,
    scrollState: VicoScrollState,
    zoomState: VicoZoomState,
) {
    val rows = bars.size
    val model = remember(bars) {
        CartesianChartModel(
            LineCartesianLayerModel.build {
                bars.forEachIndexed { index, bar ->
                    series(x = listOf(bar.cut.left, bar.cut.right), y = listOf(rows - index, rows - index))
                }
            }
        )
    }
    val lines = remember(bars) {
        bars.map { bar ->
            LineCartesianLayer.Line(
                fill = LineCartesianLayer.LineFill.single(Fill(bar.color)),
                stroke = LineCartesianLayer.LineStroke.Continuous(thickness = 4.dp)
            )
        }
    }
    val rowPlacer = VerticalAxis.ItemPlacer.step({ 1.0 })

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(lines),
                rangeProvider = CartesianLayerRangeProvider.fixed(
                    minX = scale.xMin,
                    maxX = scale.xMax,
                    minY = 1.0,
                    maxY = rows.toDouble().coerceAtLeast(2.0)
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                line = null,
                tick = null,
                guideline = null,
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    bars.getOrNull(rows - value.roundToInt())?.label ?: "–"
                },
                itemPlacer = rowPlacer,
                size = BaseAxis.Size.Fixed(ChartStartAxisWidth)
            ),
            endAxis = VerticalAxis.rememberEnd(
                line = null,
                tick = null,
                guideline = null,
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    bars.getOrNull(rows - value.roundToInt())?.cut?.format() ?: "–"
                },
                itemPlacer = rowPlacer,
                size = BaseAxis.Size.Fixed(AlphaCutsBoundsWidth)
            ),
            getXStep = { _ -> scale.tickStep }
        ),
        model = model,
        modifier = modifier,
        scrollState = scrollState,
        zoomState = zoomState,
        chartAreaHeight = 24.dp * rows.coerceAtLeast(2)
    )
}
