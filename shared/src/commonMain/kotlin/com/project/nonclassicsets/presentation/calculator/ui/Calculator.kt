package com.project.nonclassicsets.presentation.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.nonclassicsets.presentation.calculator.state.CalculatorViewModelState
import com.project.nonclassicsets.presentation.calculator.ui.FuzzyOperations.RealOperation
import com.project.nonclassicsets.presentation.calculator.viewmodel.CalculatorViewModel
import com.project.nonclassicsets.presentation.calculator.viewmodel.CalculatorViewModelCommands.ChangeParameters
import com.project.nonclassicsets.presentation.shared.FuzzyCard
import com.project.nonclassicsets.presentation.shared.FuzzyTextField
import com.project.nonclassicsets.presentation.shared.SecondaryButton
import com.project.nonclassicsets.presentation.shared.SegmentedSwitch
import com.project.nonclassicsets.presentation.shared.chart.ChartCurve
import com.project.nonclassicsets.presentation.shared.chart.MembershipChart
import com.project.nonclassicsets.presentation.shared.toDoubleInput
import com.project.nonclassicsets.presentation.theme.FuzzyLabTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import nonclassicsets.shared.generated.resources.Res
import nonclassicsets.shared.generated.resources.add_default_name
import nonclassicsets.shared.generated.resources.calculator
import nonclassicsets.shared.generated.resources.close
import nonclassicsets.shared.generated.resources.error_k1
import nonclassicsets.shared.generated.resources.error_k2
import nonclassicsets.shared.generated.resources.input_value
import nonclassicsets.shared.generated.resources.k1
import nonclassicsets.shared.generated.resources.k2
import org.jetbrains.compose.resources.stringResource

private val operations = listOf(
    "+" to RealOperation.Plus,
    "−" to RealOperation.Minus,
    "×" to RealOperation.Multiply,
    "÷" to RealOperation.Divide
)

@Composable
fun CalculatorRoot(
    onClose: () -> Unit = {},
    viewModel: CalculatorViewModel = metroViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CalculatorScreen(state = state, onChangeParameters = viewModel::onEvent, onClose = onClose)
}

@Composable
fun CalculatorScreen(
    state: CalculatorViewModelState = CalculatorViewModelState(),
    onChangeParameters: (ChangeParameters) -> Unit = {},
    onClose: () -> Unit = {},
) {
    var firstK1 by rememberSaveable { mutableStateOf("") }
    var firstK2 by rememberSaveable { mutableStateOf("") }
    var secondK1 by rememberSaveable { mutableStateOf("") }
    var secondK2 by rememberSaveable { mutableStateOf("") }
    var operationIndex by rememberSaveable { mutableIntStateOf(0) }
    var membershipInput by rememberSaveable { mutableStateOf("") }

    val parameters = ChangeParameters(
        firstK1 = firstK1.toDoubleInput(),
        firstK2 = firstK2.toDoubleInput(),
        secondK1 = secondK1.toDoubleInput(),
        secondK2 = secondK2.toDoubleInput(),
        operation = operations[operationIndex].second
    )
    LaunchedEffect(parameters) { onChangeParameters(parameters) }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.calculator),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SecondaryButton(text = stringResource(Res.string.close), onClick = onClose)
        }

        FuzzyCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OperandFields(
                    modifier = Modifier.weight(1f),
                    title = stringResource(Res.string.add_default_name, "A"),
                    k1 = firstK1,
                    k2 = firstK2,
                    onK1Change = { firstK1 = it },
                    onK2Change = { firstK2 = it }
                )
                SegmentedSwitch(
                    modifier = Modifier.width(200.dp),
                    options = operations.map { it.first },
                    selectedIndex = operationIndex,
                    onSelect = { operationIndex = it }
                )
                OperandFields(
                    modifier = Modifier.weight(1f),
                    title = stringResource(Res.string.add_default_name, "B"),
                    k1 = secondK1,
                    k2 = secondK2,
                    onK1Change = { secondK1 = it },
                    onK2Change = { secondK2 = it }
                )
            }
        }

        val scale = state.resultScale
        val result = state.result
        if (result != null && scale != null && state.resultCurve.isNotEmpty() && state.operation == parameters.operation &&
            state.firstQuantity != null && state.firstQuantity.k1 == parameters.firstK1 && state.firstQuantity.k2 == parameters.firstK2 &&
            state.secondQuantity != null && state.secondQuantity.k1 == parameters.secondK1 && state.secondQuantity.k2 == parameters.secondK2
        ) {
            FuzzyCard(modifier = Modifier.fillMaxWidth()) {

                Row {
                    MembershipChart(
                        modifier = Modifier.weight(1f),
                        curves = listOf(
                            ChartCurve(
                                state.resultCurve,
                                MaterialTheme.colorScheme.primary
                            )
                        ),
                        scale = scale,
                        alpha = null,
                        alphaLabel = ""
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FuzzyTextField(
                            value = membershipInput,
                            onValueChange = { membershipInput = it },
                            label = stringResource(Res.string.input_value)
                        )
                        val membership = membershipInput.toDoubleInput()?.let(result::membership)
                        Text(
                            text = membership?.toString().orEmpty()
                        )
                    }
                }

            }

        }
    }
}

@Composable
private fun OperandFields(
    modifier: Modifier,
    title: String,
    k1: String,
    k2: String,
    onK1Change: (String) -> Unit,
    onK2Change: (String) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        FuzzyTextField(
            value = k1,
            onValueChange = onK1Change,
            label = stringResource(Res.string.k1),
            error = if (k1.isNotEmpty() && k1.toDoubleInput()?.let { it >= 2 } != true) {
                stringResource(Res.string.error_k1)
            } else null
        )
        FuzzyTextField(
            value = k2,
            onValueChange = onK2Change,
            label = stringResource(Res.string.k2),
            error = if (k2.isNotEmpty() && k2.toDoubleInput()?.let { it > 0 } != true) {
                stringResource(Res.string.error_k2)
            } else null
        )
    }
}

@Preview
@Composable
private fun CalculatorScreenPreview() {
    FuzzyLabTheme { CalculatorScreen() }
}
