package com.project.nonclassicsets.domain.usecases

import com.gaalex.mathcore.fuzzy.AlphaLevelFuzzyValue
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.repository.FuzzyMathRepository
import com.project.nonclassicsets.presentation.calculator.ui.FuzzyOperations
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Inject
@SingleIn(AppScope::class)
class MathUseCase(
    private val mathRepo: FuzzyMathRepository
) {
    suspend fun operate(
        first: FuzzyQuantity,
        second: FuzzyQuantity,
        operation: FuzzyOperations.RealOperation
    ): Result<AlphaLevelFuzzyValue> {
        return withContext(Dispatchers.Default) {
            when (operation) {
                FuzzyOperations.RealOperation.Divide -> mathRepo.divide(first, second)
                FuzzyOperations.RealOperation.Minus -> mathRepo.subtract(first, second)
                FuzzyOperations.RealOperation.Multiply -> mathRepo.multiply(first, second)
                FuzzyOperations.RealOperation.Plus -> mathRepo.add(first, second)
            }
        }
    }

}
