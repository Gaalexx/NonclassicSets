package com.project.nonclassicsets

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform