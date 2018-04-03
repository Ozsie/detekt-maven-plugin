package com.github.ozsie

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

const val HELP = "-h"
const val CREATE_BASELINE = "-cb"
const val DEBUG = "--debug"
const val DISABLE_DEFAULT_RULE_SET = "-dd"
const val PARALLEL = "--parallel"
const val BASELINE = "-b"
const val CONFIG = "-c"
const val CONFIG_RESOURCE = "-cr"
const val FILTERS = "-f"
const val INPUT = "-i"
const val OUTPUT = "-o"
const val OUTPUT_NAME = "-on"
const val PLUGINS = "-p"

abstract class DetektMojo : AbstractMojo() {
    @Parameter(property = "detekt.baseline", defaultValue = "")
    var baseline = ""

    @Parameter(property = "detekt.config", defaultValue = "")
    var config: String = ""

    @Parameter(property = "detekt.config-resource", defaultValue = "")
    var configResource = ""

    @Parameter(property = "detekt.debug", defaultValue = "false")
    var debug = false

    @Parameter(property = "detekt.disable-default-rulesets", defaultValue = "false")
    var disableDefaultRuleSets = false

    @Parameter(property = "detekt.filters")
    var filters = ArrayList<String>()

    @Parameter(property = "detekt.help", defaultValue = "false")
    var help = false

    @Parameter(property = "detekt.input", defaultValue = "\${basedir}/src")
    var input = "\${basedir}/src"

    @Parameter(property = "detekt.output", defaultValue = "\${basedir}/detekt")
    var output = "\${basedir}/detekt"

    @Parameter(property = "detekt.output-name", defaultValue = "")
    var outputName = ""

    @Parameter(property = "detekt.parallel", defaultValue = "false")
    var parallel = false

    @Parameter(property = "detekt.plugins")
    var plugins = ArrayList<String>()

    @Parameter(defaultValue = "\${project}", readonly = true)
    var mavenProject: MavenProject? = null

    @Parameter(defaultValue = "\${settings.localRepository}", readonly = true)
    var localRepoLocation = "\${settings.localRepository}"

    fun <T> ArrayList<T>.useIf(w: Boolean, vararg value: T) = apply { if (w) addAll(value) }

    fun ArrayList<String>.buildPluginPaths(mavenProject: MavenProject?,
                                           localRepoLocation: String) = StringBuilder().apply {
        val mvnPlugin = mavenProject?.getPlugin("com.github.ozsie:detekt-maven-plugin")
        this@buildPluginPaths.forEach { plugin ->
            if (File(plugin).exists()) {
                append(plugin).append(";")
            } else {
                mvnPlugin?.dependencies
                        ?.filter { plugin == "${it.groupId}:${it.artifactId}" }
                        ?.forEach {
                            val path = localRepoLocation +
                                    "/" + it.groupId.replace(".", "/") +
                                    "/" + it.artifactId +
                                    "/" + it.version +
                                    "/" + "${it.artifactId}-${it.version}.jar"

                            append(path).append(";")
                        }
            }
        }
    }.toString().removeSuffix(";")

    fun <T> ArrayList<T>.log(): ArrayList<T> = apply {
        val sb = StringBuilder()
        forEach { sb.append(it).append(" ") }
        log.info("Args: $sb".trim())
    }
}
