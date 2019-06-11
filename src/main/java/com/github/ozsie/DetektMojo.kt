package com.github.ozsie

import org.apache.maven.model.Dependency
import org.apache.maven.model.Plugin
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

const val CREATE_BASELINE = "-cb"
const val DEBUG = "--debug"
const val DISABLE_DEFAULT_RULE_SET = "-dd"
const val PARALLEL = "--parallel"
const val BASELINE = "-b"
const val CONFIG = "-c"
const val CONFIG_RESOURCE = "-cr"
const val INPUT = "-i"
const val PLUGINS = "-p"
const val REPORT = "-r"
const val BUILD_UPON_DEFAULT_CONFIG = "--build-upon-default-config"
const val FAIL_FAST = "--fail-fast"
const val AUTO_CORRECT = "-ac"
const val CLASS_PATH = "-cp"
const val EXCLUDES = "-ex"
const val INCLUDES = "-in"
const val JVM_TARGET = "--jvm-target"

const val MDP_ID = "com.github.ozsie:detekt-maven-plugin"

const val DOT = "."
const val SLASH = "/"
const val SEMICOLON = ";"

abstract class DetektMojo : AbstractMojo() {

    @Parameter(defaultValue = "false")
    var skip = false

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

    @Parameter(property = "detekt.help", defaultValue = "false")
    var help = false

    @Parameter(property = "detekt.input", defaultValue = "\${basedir}/src")
    var input = "\${basedir}/src"

    @Parameter(property = "detekt.parallel", defaultValue = "false")
    var parallel = false

    @Parameter(property = "detekt.disableDefaultRuleset", defaultValue = "false")
    var disableDefaultRuleset = false

    @Parameter(property = "detekt.buildUponDefaultConfig", defaultValue = "false")
    var buildUponDefaultConfig = false

    @Parameter(property = "detekt.failFast", defaultValue = "false")
    var failFast = false

    @Parameter(property = "detekt.report")
    var report = ArrayList<String>()

    @Parameter(property = "detekt.plugins")
    var plugins = ArrayList<String>()

    @Parameter(property = "detekt.autoCorrect")
    var autoCorrect = false

    @Parameter(property = "detekt.classPath")
    var classPath = ""

    @Parameter(property = "detekt.excludes")
    var excludes = ""

    @Parameter(property = "detekt.includes")
    var includes = ""

    @Parameter(property = "detekt.jvmTarget")
    var jvmTarget = ""

    @Parameter(defaultValue = "\${project}", readonly = true)
    var mavenProject: MavenProject? = null

    @Parameter(defaultValue = "\${settings.localRepository}", readonly = true)
    var localRepoLocation = "\${settings.localRepository}"

    internal fun getCliSting() = ArrayList<String>().apply {
        useIf(debug, DEBUG)
                .useIf(disableDefaultRuleSets, DISABLE_DEFAULT_RULE_SET)
                .useIf(parallel, PARALLEL)
                .useIf(baseline.isNotEmpty(), BASELINE, baseline)
                .useIf(config.isNotEmpty(), CONFIG, resolveConfig(mavenProject, config))
                .useIf(configResource.isNotEmpty(), CONFIG_RESOURCE, configResource)
                .useIf(input.isNotEmpty(), INPUT, input)
                .useIf(report.isNotEmpty(), toArgList(report))
                .useIf(buildUponDefaultConfig, BUILD_UPON_DEFAULT_CONFIG)
                .useIf(failFast, FAIL_FAST)
                .useIf(disableDefaultRuleset, DISABLE_DEFAULT_RULE_SET)
                .useIf(plugins.isNotEmpty(), PLUGINS, plugins.buildPluginPaths(mavenProject, localRepoLocation))
                .useIf(autoCorrect, AUTO_CORRECT)
                .useIf(classPath.isNotEmpty(), CLASS_PATH)
                .useIf(excludes.isNotEmpty(), EXCLUDES)
                .useIf(includes.isNotEmpty(), INCLUDES)
                .useIf(jvmTarget.isNotEmpty(), JVM_TARGET)
    }

    internal fun <T> ArrayList<T>.log(): ArrayList<T> = log(this@DetektMojo.log)
}

private fun toArgList(list: List<String>) = ArrayList<String>().apply {
    list.forEach {
        add(REPORT)
        add(it)
    }
}

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

internal infix fun Dependency.asPath(root: String) =
        "$root/${groupId.asPath()}/$artifactId/$version/$artifactId-$version.jar"

internal fun Dependency.getIdentifier() = "$groupId:$artifactId"

internal fun String.asPath() = replace(DOT, SLASH)

internal fun resolveConfig(project: MavenProject?, config: String): String {
    if (project == null) return config
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
                throw RuntimeException("Cannot find the config ${provided.absolutePath} or ${parent.absolutePath}")
            }
        }
    } else {
        confLocation = provided.absolutePath
    }
    return confLocation
}