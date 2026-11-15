package com.project.nonclassicsets.data.repository

import com.project.nonclassicsets.domain.repository.LogRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.sink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import kotlinx.io.buffered
import kotlinx.io.writeString
import kotlin.time.Clock

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class LogRepositoryImpl : LogRepository {
    companion object {
        private const val FILE_NAME = "Log.txt"

        private val TIMESTAMP_FORMAT = LocalDateTime.Format {
            date(LocalDate.Formats.ISO)
            char(' ')
            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    }

    private val file = PlatformFile(FILE_NAME)
    private val entries = Channel<String>(Channel.UNLIMITED)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            for (entry in entries) {
                append(entry)
            }
        }
    }

    override fun log(message: String) {
        entries.trySend("[${timestamp()}] $message\n")
    }

    private fun append(entry: String) {
        try {
            file.sink(append = true).buffered().use { it.writeString(entry) }
        } catch (e: IOException) {
            println("Failed to write $FILE_NAME: ${e.message}")
        }
    }

    private fun timestamp(): String {
        return Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .format(TIMESTAMP_FORMAT)
    }
}
