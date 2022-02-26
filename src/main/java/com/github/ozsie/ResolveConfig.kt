package com.github.ozsie

import org.apache.maven.project.MavenProject
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

const val EXPORTED_FILE_LOCATION = "/remote_detekt_config.yml"

internal fun resolveConfig(project: MavenProject?, config: String): String {
    if (project == null) return config
    if (config.startsWith("http")) {
        return getFileFromUrl(project, config)
    }
    return config.split(',', ';')
        .joinToString(separator = ";") { resolveSingle(project, it) }
}

private fun getFileFromUrl(project: MavenProject, urlString: String) : String {
    val url = URL(urlString)
    url.openStream().use { Files.copy(it, Paths.get(project.basedir.absolutePath + EXPORTED_FILE_LOCATION), StandardCopyOption.REPLACE_EXISTING) }
    return File(EXPORTED_FILE_LOCATION).absolutePath
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
