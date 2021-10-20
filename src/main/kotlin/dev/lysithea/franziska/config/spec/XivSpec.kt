package dev.lysithea.franziska.config.spec

import com.uchuhimo.konf.ConfigSpec

object XivSpec : ConfigSpec() {
    val fflogsToken by required<String>(name = "fflogs", description = "fflogs token")
}
