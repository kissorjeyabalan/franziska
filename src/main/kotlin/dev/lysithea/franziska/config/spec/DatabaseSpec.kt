package dev.lysithea.franziska.config.spec

import com.uchuhimo.konf.ConfigSpec

object DatabaseSpec : ConfigSpec() {
    val host by required<String>(description = "postgresql host")
    val database by required<String>(description = "postgresql database")
    val username by required<String>(description = "postgresql username")
    val password by required<String>(description = "postgresql password")
    val port by required<Int>(description = "postgresql port")
    val poolSize by optional(8, description = "postgresql max pool size")
}
