package dev.lysithea.franziska.config

import java.util.*

class BuildInfo {
    private val props = Properties()
    val version: String by lazy {
        props.getProperty("version")
    }

    fun load(): BuildInfo {
        props.load(Thread.currentThread().contextClassLoader.getResourceAsStream("build.properties"))
        return this
    }
}

val buildInfo = BuildInfo().load()
