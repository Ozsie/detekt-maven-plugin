package com.github.ozsie

import org.apache.maven.MavenExecutionException
import org.apache.maven.model.Plugin
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject

internal fun <T> ArrayList<T>.log(log: Log): ArrayList<T> = apply {
    StringBuilder().apply {
        forEach { append(it).append(" ") }
        log.info("Args: $this".trim())
    }
}

internal fun <T> ArrayList<T>.useIf(w: Boolean, vararg value: T) = apply { if (w) addAll(value) }

internal fun <T> ArrayList<T>.useIf(w: Boolean, value: List<T>) = apply { if (w) addAll(value) }

internal fun ArrayList<String>.buildPluginPaths(mavenProject: MavenProject?, localRepoLocation: String, log: Log) =
        StringBuilder().apply {
            mavenProject?.let {
                val plugin: Plugin? = if (it.pluginManagement != null &&
                    it.pluginManagement.pluginsAsMap[MDP_ID] != null) {
                        log.debug("Plugin Management")
                        it.pluginManagement.pluginsAsMap[MDP_ID]
                } else {
                    log.debug("Plugin")
                    it.getPlugin(MDP_ID)
                }
                if (plugin != null) {
                    log.debug("$plugin")
                    this.buildPluginPaths(this@buildPluginPaths, plugin, localRepoLocation)
                } else throw MavenExecutionException("No plugin defined", NullPointerException())
            }
        }.toString().removeSuffix(SEMICOLON)
