package com.project.nonclassicsets.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.project.nonclassicsets.presentation.adddialog.ui.AddQuantityDialogRoot
import com.project.nonclassicsets.presentation.calculator.ui.CalculatorRoot
import com.project.nonclassicsets.presentation.clusteringresult.ui.ClusteringResultRoot
import com.project.nonclassicsets.presentation.generatedialog.ui.GenerateDialogRoot
import com.project.nonclassicsets.presentation.workingset.ui.WorkingSetRoot
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val navConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(ScreenTypes.WorkingSet::class, ScreenTypes.WorkingSet.serializer())
            subclass(ScreenTypes.Calculator::class, ScreenTypes.Calculator.serializer())
            subclass(ScreenTypes.ClusteringResult::class, ScreenTypes.ClusteringResult.serializer())
            subclass(ScreenTypes.GenerateQuantities::class, ScreenTypes.GenerateQuantities.serializer())
            subclass(ScreenTypes.AddQuantity::class, ScreenTypes.AddQuantity.serializer())
        }
    }
}

@Composable
fun AppNavigation() {
    val navBackStack = rememberNavBackStack(navConfiguration, ScreenTypes.WorkingSet)
    val navEntryProvider = entryProvider<NavKey> {
        entry<ScreenTypes.WorkingSet> {
            WorkingSetRoot(
                navigateToGenerate = { navBackStack.add(ScreenTypes.GenerateQuantities) },
                navigateToAdd = { navBackStack.add(ScreenTypes.AddQuantity) },
                navigateToResult = { navBackStack.add(ScreenTypes.ClusteringResult) },
                navigateToCalculator = { navBackStack.add(ScreenTypes.Calculator) }
            )
        }
        entry<ScreenTypes.Calculator> {
            CalculatorRoot(onClose = { navBackStack.removeLastOrNull() })
        }
        entry<ScreenTypes.ClusteringResult> {
            ClusteringResultRoot(onClose = { navBackStack.removeLastOrNull() })
        }
        entry<ScreenTypes.GenerateQuantities>(metadata = DialogSceneStrategy.dialog()) {
            GenerateDialogRoot(onDismiss = { navBackStack.removeLastOrNull() })
        }
        entry<ScreenTypes.AddQuantity>(metadata = DialogSceneStrategy.dialog()) {
            AddQuantityDialogRoot(onDismiss = { navBackStack.removeLastOrNull() })
        }
    }

    NavDisplay(
        backStack = navBackStack,
        onBack = { navBackStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        sceneStrategies = listOf(DialogSceneStrategy(), SinglePaneSceneStrategy()),
        entryProvider = navEntryProvider
    )
}
