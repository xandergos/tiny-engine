package dev.xanhub.tinyengine.util

import java.io.File
import java.io.FileNotFoundException

object Resource {
    fun get(path: String): File {
        val url = this::class.java.classLoader.getResource(path) ?: throw FileNotFoundException()
        return File(url.toURI())
    }
}