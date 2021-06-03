package dev.lysithea.franziska.core.config

/**
 * Representation of application configuration used for injecting from resources.
 */
data class Config(val franziska: FranziskaConfig, val mongo: MongoConfig)