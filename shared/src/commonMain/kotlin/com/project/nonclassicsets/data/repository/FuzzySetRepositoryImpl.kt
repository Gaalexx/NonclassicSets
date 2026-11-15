package com.project.nonclassicsets.data.repository

import com.project.nonclassicsets.data.files.LogMessages
import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import com.project.nonclassicsets.domain.repository.FuzzySetRepository
import com.project.nonclassicsets.domain.repository.LogRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class FuzzySetRepositoryImpl(
    private val logRepo: LogRepository
) : FuzzySetRepository {
    private var idCounter = 0

    private val _quantities = MutableStateFlow<List<FuzzyQuantity>>(emptyList())
    override val quantities = _quantities.asStateFlow()

    private val _lastResult = MutableStateFlow<ClusteringOutcome?>(null)
    override val lastResult: StateFlow<ClusteringOutcome?> = _lastResult.asStateFlow()

    override fun nextId(): String = idFromIndex(idCounter)

    override fun add(drafts: List<FuzzyQuantityDraft>) {
        if (drafts.isEmpty()) return
        val added = drafts.map(::toQuantity)
        _quantities.update { it + added }
        logRepo.log(LogMessages.added(added))
    }

    override fun replace(drafts: List<FuzzyQuantityDraft>) {
        idCounter = 0
        val quantities = drafts.map(::toQuantity)
        _quantities.value = quantities
        logRepo.log(LogMessages.replaced(quantities))
    }

    override fun remove(id: String) {
        val removed = _quantities.value.find { it.id == id } ?: return
        _quantities.update { list -> list - removed }
        logRepo.log(LogMessages.removed(removed))
        if (_quantities.value.isEmpty()) idCounter = 0
    }

    override fun saveResult(result: ClusteringOutcome) {
        _lastResult.value = result
        logRepo.log(LogMessages.clustered(result))
    }

    private fun toQuantity(draft: FuzzyQuantityDraft): FuzzyQuantity {
        return FuzzyQuantity(
            id = idFromIndex(idCounter++),
            name = draft.name?.trim()?.takeIf { it.isNotEmpty() },
            k1 = draft.k1,
            k2 = draft.k2
        )
    }

    private fun idFromIndex(index: Int): String {
        var rest = index
        return buildString {
            do {
                insert(0, 'A' + rest % 26)
                rest = rest / 26 - 1
            } while (rest >= 0)
        }
    }
}
