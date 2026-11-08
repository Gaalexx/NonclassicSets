package com.project.nonclassicsets.data.repository

import com.project.nonclassicsets.data.files.TxtQuantitiesParser
import com.project.nonclassicsets.data.files.PlatformFileRef
import com.project.nonclassicsets.domain.model.FileRef
import com.project.nonclassicsets.domain.model.ImportPreview
import com.project.nonclassicsets.domain.repository.FilesRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.CancellationException

@Inject
@ContributesBinding(AppScope::class)
class FilesRepositoryImpl : FilesRepository {
    companion object {
        private const val TXT = "txt"
    }

    override suspend fun pickTxtFile(): FileRef? {
        return FileKit.openFilePicker(type = FileKitType.File(TXT))?.let(::PlatformFileRef)
    }

    override suspend fun readTxt(file: FileRef): Result<ImportPreview> {
        return try {
            val lines = file.platformFile().readString().lines()
            Result.success(TxtQuantitiesParser.parse(file.name, lines))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun FileRef.platformFile() = (this as PlatformFileRef).file
}
