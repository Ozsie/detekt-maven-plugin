package com.github.ozsie

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

const val MDP_ID = "com.github.ozsie:detekt-maven-plugin"

const val DOT = "."
const val SLASH = "/"
const val SEMICOLON = ";"

abstract class DetektMojo : AbstractMojo() {

    @Parameter(property= "detekt.skip", defaultValue = "false")
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

    @Parameter(property = "detekt.failBuildOnMaxIssuesReached", defaultValue = "true")
    var failBuildOnMaxIssuesReached = true

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

    @Parameter(property = "detekt.languageVersion")
    var languageVersion = ""

    @Parameter(property = "detekt.allRules")
    var allRules = false

    @Parameter(property = "detekt.basePath")
    var basePath = ""

    @Parameter(property = "detekt.maxIssues")
    var maxIssues = ""

    @Parameter(defaultValue = "\${project}", readonly = true)
    var mavenProject: MavenProject? = null

    @Parameter(defaultValue = "\${settings.localRepository}", readonly = true)
    var localRepoLocation = "\${settings.localRepository}"

    fun debug(message: String) = log.debug(message)

    internal fun inputSkipLog(skip: Boolean) {
        if (skip) log.info("Skipping execution") else log.info("Input directory '$input' not found, skipping module")
    }
}
