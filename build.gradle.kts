import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application

    kotlin("jvm")
    kotlin("plugin.serialization")

    id("com.github.jakemarsden.git-hooks")
    id("com.github.johnrengelman.shadow")
    id("io.gitlab.arturbosch.detekt")
}

group = "dev.lysithea"
version = "2.0.0"

repositories {
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
}

dependencies {
    detektPlugins(libs.detekt)
    implementation(libs.kotlin.stdlib)

    implementation(libs.kord.extensions)

    // Konf
    implementation(libs.konf.core)
    implementation(libs.konf.yaml)

    // Logging dependencies
    implementation(libs.groovy)
    implementation(libs.logback)
    implementation(libs.logging)
    implementation(libs.sentry)
}

application {
    // This is deprecated, but the Shadow plugin requires it
    mainClassName = "dev.lysithea.franziska.AppKt"
}

gitHooks {
    setHooks(
        mapOf("pre-commit" to "detekt")
    )
}

tasks.withType<KotlinCompile> {
    // Current LTS version of Java
    kotlinOptions.jvmTarget = "11"

    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "dev.lysithea.franziska.AppKt"
        )
    }
}

java {
    // Current LTS version of Java
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

detekt {
    buildUponDefaultConfig = true
    config = rootProject.files("detekt.yml")
}

val generateBuildInfo = task("generateBuildInfo") {
    outputs.file("src/main/resources/build.properties")
    doLast {
        File("src/main/resources/build.properties").writeText(
            "version=${project.version}"
        )
    }
}

tasks.processResources {
    dependsOn("generateBuildInfo")
    from("src/main/resources/build.properties")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}