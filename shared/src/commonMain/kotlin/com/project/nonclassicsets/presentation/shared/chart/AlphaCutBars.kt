package com.project.nonclassicsets.presentation.shared.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.project.nonclassicsets.domain.model.AlphaCut
import com.project.nonclassicsets.presentation.shared.format

@Immutable
data class AlphaCutBar(
    val label: String,
    val cut: AlphaCut,
    val color: Color
)

private val BarRowHeight = 24.dp
private val BarLabelGap = 12.dp

@Composable
fun AlphaCutBars(
    modifier: Modifier = Modifier,
    bars: List<AlphaCutBar>,
    scale: ChartScale,
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    val density = LocalDensity.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        bars.forEach { bar ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BarRowHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.width(ChartPlotStart)) {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = bar.label,
                        color = bar.color,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = ChartPlotEnd)
                        .fillMaxSize()
                ) {
                    val widthPx = with(density) { maxWidth.toPx() }
                    val leftPx = (bar.cut.left / scale.xMax * widthPx).toFloat().coerceIn(0f, widthPx)
                    val rightPx = (bar.cut.right / scale.xMax * widthPx).toFloat().coerceIn(0f, widthPx)

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val y = size.height / 2f
                        val tick = 5.dp.toPx()
                        val stroke = 3.dp.toPx()
                        drawLine(bar.color, Offset(leftPx, y), Offset(rightPx, y), strokeWidth = stroke)
                        drawLine(bar.color, Offset(leftPx, y - tick), Offset(leftPx, y + tick), strokeWidth = 1.5.dp.toPx())
                        drawLine(bar.color, Offset(rightPx, y - tick), Offset(rightPx, y + tick), strokeWidth = 1.5.dp.toPx())
                    }

                    val label = bar.cut.format()
                    val labelWidth = textMeasurer.measure(label, labelStyle).size.width
                    val gapPx = with(density) { BarLabelGap.toPx() }
                    val labelX = if (rightPx + gapPx + labelWidth <= widthPx) {
                        rightPx + gapPx
                    } else {
                        (leftPx - gapPx - labelWidth).coerceAtLeast(0f)
                    }

                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = with(density) { labelX.toDp() }),
                        text = label,
                        style = labelStyle,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
