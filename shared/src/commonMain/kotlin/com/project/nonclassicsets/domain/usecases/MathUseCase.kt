package com.project.nonclassicsets.domain.usecases

import com.project.nonclassicsets.domain.model.AlphaCut
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
    ): Result<List<AlphaCut>> {
        return withContext(Dispatchers.Default) {
            when (operation) {
                FuzzyOperations.RealOperation.Divide -> mathRepo.divide(first, second)
                FuzzyOperations.RealOperation.Minus -> mathRepo.subtract(first, second)
                FuzzyOperations.RealOperation.Multiply -> mathRepo.multiply(first, second)
                FuzzyOperations.RealOperation.Plus -> mathRepo.add(first, second)
            }
        }
    }

    suspend fun membership(
        curve: List<Double>,
        member: Double
    ): Result<Double> {
        return withContext(Dispatchers.Default) {
            mathRepo.membershipAlpha(
                buildList<AlphaCut> {
                    var leftPtr = 0
                    var rightPtr = curve.lastIndex
                    while (leftPtr <= rightPtr) {
                        add(AlphaCut(left = curve[leftPtr++], right = curve[rightPtr--]))
                    }
                },
                member = member
            )
        }
    }
}
