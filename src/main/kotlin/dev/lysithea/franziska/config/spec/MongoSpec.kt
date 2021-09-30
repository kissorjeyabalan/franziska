package dev.lysithea.franziska.config.spec

import com.uchuhimo.konf.ConfigSpec

object MongoSpec : ConfigSpec() {
    val username by required<String>(description = "mongodb username")
    val password by required<String>(description = "mongodb password")
    val host by required<String>(description = "mongodb host")
    val port by required<Int>(description = "mongodb port")
    val db by required<String>(description = "default database")
}
