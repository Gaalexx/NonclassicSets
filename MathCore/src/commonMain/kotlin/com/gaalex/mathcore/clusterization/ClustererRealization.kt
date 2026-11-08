package com.gaalex.mathcore.clusterization

import com.gaalex.mathcore.interval.Interval

class AgglomerativeClusterer(
    private val distance: IntervalDistance = EndpointIntervalDistance
) : IntervalClusterer {

    override fun cluster(
        intervals: List<Interval>, mode: ClusterMode
    ): ClusteringResult {
        require(intervals.isNotEmpty()) { "Intervals must not be empty" }

        val applied = when (mode) {
            is ClusterMode.ByCount -> {
                require(mode.count in 1..intervals.size) {
                    "Cluster count must be in 1..${intervals.size}"
                }
                buildHierarchy(intervals).take(intervals.size - mode.count)
            }

            is ClusterMode.ByThreshold -> {
                require(mode.epsilon.isFinite() && mode.epsilon >= 0.0) {
                    "Epsilon must be finite and non-negative"
                }
                buildHierarchy(intervals).takeWhile { it.distance <= mode.epsilon }
            }
        }

        return cutHierarchy(intervals.size, applied)
    }

    private class Merge(val first: Int, val second: Int, val distance: Double)

    private fun buildHierarchy(intervals: List<Interval>): List<Merge> {
        val n = intervals.size
        val capacity = 2 * n - 1

        val distances = Array(capacity) { DoubleArray(capacity) }
        val active = BooleanArray(capacity) { it < n }
        val sizes = IntArray(capacity) { if (it < n) 1 else 0 }
        val representatives = IntArray(capacity) { it }

        for (i in 0 until n) {
            for (j in i + 1 until n) {
                val value = distance.distance(intervals[i], intervals[j])
                require(value.isFinite() && value >= 0.0) {
                    "Distance between $i and $j must be finite and non-negative"
                }
                distances[i][j] = value
                distances[j][i] = value
            }
        }

        val chain = ArrayDeque<Int>()
        val history = mutableListOf<Merge>()

        while (history.size < n - 1) {
            if (chain.isEmpty()) {
                chain.addLast(active.indexOfFirst { it })
            }

            val a = chain.last()
            val previous = chain.getOrNull(chain.lastIndex - 1)

            var b = previous ?: -1
            var nearestDistance = previous?.let { distances[a][it] } ?: Double.POSITIVE_INFINITY

            for (candidate in active.indices) {
                if (active[candidate] && candidate != a && distances[a][candidate] < nearestDistance) {
                    b = candidate
                    nearestDistance = distances[a][candidate]
                }
            }

            if (b != previous) {
                chain.addLast(b)
                continue
            }

            chain.removeLast()
            chain.removeLast()

            val merged = n + history.size
            history.add(Merge(representatives[a], representatives[b], nearestDistance))

            active[a] = false
            active[b] = false
            sizes[merged] = sizes[a] + sizes[b]
            representatives[merged] = minOf(representatives[a], representatives[b])

            val weightA = sizes[a].toDouble() / sizes[merged]
            val weightB = sizes[b].toDouble() / sizes[merged]

            for (other in 0 until merged) {
                if (!active[other]) continue
                val updated = weightA * distances[a][other] + weightB * distances[b][other]
                distances[merged][other] = updated
                distances[other][merged] = updated
            }

            active[merged] = true
        }

        return history.sortedBy { it.distance }
    }

    private fun cutHierarchy(objectCount: Int, merges: List<Merge>): ClusteringResult {
        val groups = DSU(objectCount)
        merges.forEach { groups.union(it.first, it.second) }

        val clusters = (0 until objectCount).groupBy(groups::find).values.toList()

        return ClusteringResult(clusters = clusters)
    }

    private class DSU(size: Int) {
        private val parent = IntArray(size) { it }

        fun find(element: Int): Int {
            var current = element
            while (parent[current] != current) {
                parent[current] = parent[parent[current]]
                current = parent[current]
            }
            return current
        }

        fun union(first: Int, second: Int) {
            parent[find(first)] = find(second)
        }
    }
}
