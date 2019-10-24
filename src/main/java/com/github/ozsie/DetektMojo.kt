package com.github.ozsie

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject



const val AUTO_CORRECT = "-ac"
const val BASELINE = "-b"
const val BUILD_UPON_DEFAULT_CONFIG = "--build-upon-default-config"
const val CREATE_BASELINE = "-cb"
const val CLASS_PATH = "-cp"
const val CONFIG = "-c"
const val CONFIG_RESOURCE = "-cr"
const val DEBUG = "--debug"
const val DISABLE_DEFAULT_RULE_SET = "-dd"
const val EXCLUDES = "-ex"
const val FAIL_FAST = "--fail-fast"
const val INCLUDES = "-in"
const val INPUT = "-i"
const val JVM_TARGET = "--jvm-target"
const val PARALLEL = "--parallel"
const val PLUGINS = "-p"
const val REPORT = "-r"
const val LANGUAGE_VERSION = "--language-version"

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
    lateinit var jvmTarget: JvmTarget

    @Parameter(property = "detekt.languageVersion")
    lateinit var languageVersion: JvmTarget

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
                .useIf(classPath.isNotEmpty(), CLASS_PATH, classPath)
                .useIf(excludes.isNotEmpty(), EXCLUDES, excludes)
                .useIf(includes.isNotEmpty(), INCLUDES, includes)
                .jvmTargetIfInitialized()
                .languageVersionIfInitialized()
    }

    private fun ArrayList<String>.jvmTargetIfInitialized() = apply {
        if (::jvmTarget.isInitialized) {
            add(JVM_TARGET)
            add(jvmTarget.value)
        }
    }

    private fun ArrayList<String>.languageVersionIfInitialized() = apply {
        if (::languageVersion.isInitialized) {
            add(LANGUAGE_VERSION)
            add(languageVersion.value)
        }
    }

    internal fun <T> ArrayList<T>.log(): ArrayList<T> = log(this@DetektMojo.log)
}

private fun toArgList(list: List<String>) = ArrayList<String>().apply {
    list.forEach {
        add(REPORT)
        add(it)
    }
}
