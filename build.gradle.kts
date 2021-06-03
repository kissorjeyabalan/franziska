import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
    application
}

group = "dev.lysithea.franziska"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.5.0-RC")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.2.1")
    implementation(kotlin("reflect"))

    // Http
    implementation("io.ktor", "ktor-client")
    implementation("io.ktor", "ktor-client-okhttp")
    implementation("io.ktor", "ktor-client-serialization-jvm")
    implementation(platform("io.ktor:ktor-bom:1.5.3"))

    implementation("dev.kord.x:emoji:0.5.0-SNAPSHOT")

    // Logging
    implementation("io.github.microutils", "kotlin-logging", "1.12.5")
    implementation("org.slf4j", "slf4j-api", "2.0.0alpha1")
    implementation("ch.qos.logback", "logback-classic", "1.3.0-alpha5")

    // Database
    implementation("org.litote.kmongo", "kmongo-coroutine-serialization", "4.2.7")

    // Kord
    implementation("dev.kord", "kord-core", "kotlin-1.5-SNAPSHOT") {
        version {
            strictly("kotlin-1.5-SNAPSHOT")
        }
    }

    // Utility
    implementation("net.sf.trove4j", "trove4j", "3.0.3")
    implementation("io.insert-koin", "koin-core", "3.0.2")
    implementation("io.projectreactor", "reactor-core", "3.4.4")
    implementation("io.projectreactor.kotlin", "reactor-kotlin-extensions", "1.1.3")

    // Configuration
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.12.+")
    implementation("com.sksamuel.hoplite", "hoplite-core", "1.4.0")
    implementation("com.sksamuel.hoplite", "hoplite-yaml", "1.4.0")

    application {
        mainClass.set("dev.lysithea.franziska.LauncherKt")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs +
                        "-Xopt-in=kotlin.RequiresOptIn" +
                        "-Xopt-in=dev.kord.common.annotation.KordPreview" +
                        "-Xopt-in=dev.kord.common.annotation.KordExperimental"
            }
        }

        installDist {
            destinationDir = buildDir.resolve("libs/install")
        }
    }
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}