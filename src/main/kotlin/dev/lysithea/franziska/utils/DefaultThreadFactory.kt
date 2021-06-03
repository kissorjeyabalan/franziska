@file:Suppress("unused")
package dev.lysithea.franziska.utils

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation of default [java.util.concurrent.Executors] with thread name.
 * @see java.util.concurrent.Executors
 */
class DefaultThreadFactory(name: String?) : ThreadFactory {
    private val threadNumber = AtomicInteger(1)
    private val threadGroup: ThreadGroup
    private val threadPrefix: String

    init {
        val securityManager = System.getSecurityManager()
        threadGroup = if (securityManager != null) {
            securityManager.threadGroup
        } else {
            Thread.currentThread().threadGroup
        }

        threadPrefix = (if (name != null) "$name-" else "") + "pool-${pool.getAndIncrement()}-thread-"
    }

    /**
     * Creates a new [Thread].
     * @return the created [Thread].
     * @see Thread
     */
    override fun newThread(runnable: Runnable): Thread {
        val thread = Thread(threadGroup, runnable, "$threadPrefix${threadNumber.getAndIncrement()}", 0)
        if (thread.isDaemon) thread.isDaemon = false
        if (thread.priority != Thread.NORM_PRIORITY) thread.priority = Thread.NORM_PRIORITY
        return thread
    }

    companion object {
        private val pool = AtomicInteger(1)

        /**
         * Creates a new [Executors.newSingleThreadExecutor] with [name].
         *
         * @param name for thread pool.
         * @return the created [ExecutorService].
         */
        fun newSingleThreadExecutor(name: String?): ExecutorService =
            Executors.newSingleThreadExecutor(DefaultThreadFactory(name))
    }
}