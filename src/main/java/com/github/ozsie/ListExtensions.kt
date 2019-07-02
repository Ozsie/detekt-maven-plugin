package com.github.ozsie

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

internal fun ArrayList<String>.buildPluginPaths(mavenProject: MavenProject?, localRepoLocation: String) =
        StringBuilder().apply {
            mavenProject?.let {
                this.buildPluginPaths(this@buildPluginPaths, it.getPlugin(MDP_ID), localRepoLocation)
            }
        }.toString().removeSuffix(SEMICOLON)