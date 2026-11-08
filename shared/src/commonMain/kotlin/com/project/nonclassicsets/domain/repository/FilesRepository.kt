package com.project.nonclassicsets.domain.repository

import com.project.nonclassicsets.domain.model.FileRef
import com.project.nonclassicsets.domain.model.ImportPreview

interface FilesRepository {
    suspend fun pickTxtFile(): FileRef?
    suspend fun readTxt(file: FileRef): Result<ImportPreview>
}
