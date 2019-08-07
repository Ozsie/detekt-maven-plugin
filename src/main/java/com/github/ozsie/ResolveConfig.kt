package com.github.ozsie

import org.apache.maven.project.MavenProject
import java.io.File
import java.io.FileNotFoundException

internal fun resolveConfig(project: MavenProject?, config: String): String {
    if (project == null) return config
    return config.split(',', ';')
        .joinToString(separator = ";") { resolveSingle(project, it) }
}

private fun resolveSingle(project: MavenProject, config: String): String {
    val confLocation: String
    // look at provided path in case it's absolute
    val provided = File(config).absoluteFile
    if (!provided.exists()) {
        // look for the file relative to the project basedir
        val projectLocal = File(project.basedir, config)
        confLocation = if (projectLocal.exists()) {
            projectLocal.absolutePath
        } else {
            // look for the file in the directory above the project (in case of multimodule projects)
            val parent = File(project.basedir.parentFile, config)
            if (parent.exists()) {
                parent.absolutePath
            } else {
                throw FileNotFoundException("Cannot find the config ${provided.absolutePath} or ${parent.absolutePath}")
            }
        }
    } else {
        confLocation = provided.absolutePath
    }
    return confLocation
}
