package com.project.nonclassicsets.data.files

import com.project.nonclassicsets.domain.model.FileRef
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name

class PlatformFileRef(val file: PlatformFile) : FileRef {
    override val name: String
        get() = file.name
}
