package dev.lysithea.franziska.core.config

data class MongoConfig(
    val host: String,
    val port: String,
    val database: String,
    val username: String,
    val password: String
) {
    val connectionString: String
        get() = "mongodb://$username:$password@$host:$port/$database"
}