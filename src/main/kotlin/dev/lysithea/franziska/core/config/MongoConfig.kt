package dev.lysithea.franziska.core.config

/**
 * Representation of MongoDB configuration.
 *
 * @property host the hostname for mongodb.
 * @property port the port mongodb.
 * @property database the database to use.
 * @property username the username to mongodb.
 * @property password the password to mongodb.
 */
data class MongoConfig(
    val host: String,
    val port: String,
    val database: String,
    val username: String,
    val password: String
) {
    /**
     * Connection string to use for mongodb connection.
     */
    val connectionString: String
        get() = "mongodb://$username:$password@$host:$port/$database"
}