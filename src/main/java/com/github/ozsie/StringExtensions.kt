package com.github.ozsie

import org.apache.maven.model.Plugin
import java.io.File

internal fun StringBuilder.buildPluginPaths(plugins: ArrayList<String>, mdp: Plugin, root: String) {
    plugins.forEach { plugin ->
        if (File(plugin).exists()) {
            append(plugin).append(SEMICOLON)
        } else {
            mdp.dependencies
                    ?.filter { plugin == it.getIdentifier() }
                    ?.forEach { append(it asPath root).append(SEMICOLON) }
        }
    }
}

internal fun String.asPath() = replace(DOT, SLASH)
