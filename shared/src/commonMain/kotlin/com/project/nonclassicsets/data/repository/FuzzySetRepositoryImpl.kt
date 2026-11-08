package com.project.nonclassicsets.data.repository

import com.project.nonclassicsets.domain.model.ClusteringOutcome
import com.project.nonclassicsets.domain.model.DatasetSource
import com.project.nonclassicsets.domain.model.FuzzyQuantity
import com.project.nonclassicsets.domain.model.FuzzyQuantityDraft
import com.project.nonclassicsets.domain.repository.FuzzySetRepository
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
class FuzzySetRepositoryImpl : FuzzySetRepository {
    companion object {
        private const val DEFAULT_NAME = "Величина"
    }

    private var idCounter = 0

    private val _quantities = MutableStateFlow<List<FuzzyQuantity>>(emptyList())
    override val quantities = _quantities.asStateFlow()

    private val _source = MutableStateFlow<DatasetSource>(DatasetSource.Empty)
    override val source = _source.asStateFlow()

    private val _lastResult = MutableStateFlow<ClusteringOutcome?>(null)
    override val lastResult: StateFlow<ClusteringOutcome?> = _lastResult.asStateFlow()

    override fun nextId(): String = idFromIndex(idCounter)

    override fun add(drafts: List<FuzzyQuantityDraft>, source: DatasetSource) {
        if (drafts.isEmpty()) return
        val wasEmpty = _quantities.value.isEmpty()
        _quantities.update { it + drafts.map(::toQuantity) }
        _source.update { current ->
            when {
                wasEmpty -> source
                current == source -> current
                else -> DatasetSource.Mixed
            }
        }
    }

    override fun replace(drafts: List<FuzzyQuantityDraft>, source: DatasetSource) {
        idCounter = 0
        _quantities.value = drafts.map(::toQuantity)
        _source.value = if (drafts.isEmpty()) DatasetSource.Empty else source
    }

    override fun remove(id: String) {
        _quantities.update { list -> list.filterNot { it.id == id } }
        if (_quantities.value.isEmpty()) {
            idCounter = 0
            _source.value = DatasetSource.Empty
        }
    }

    override fun saveResult(result: ClusteringOutcome) {
        _lastResult.value = result
    }

    private fun toQuantity(draft: FuzzyQuantityDraft): FuzzyQuantity {
        val id = idFromIndex(idCounter++)
        return FuzzyQuantity(
            id = id,
            name = draft.name?.takeIf { it.isNotBlank() }?.trim() ?: "$DEFAULT_NAME $id",
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
