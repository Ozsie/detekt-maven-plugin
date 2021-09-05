package com.github.ozsie

import io.github.detekt.tooling.api.spec.RulesSpec
import io.github.detekt.tooling.dsl.ProcessingSpecBuilder
import io.gitlab.arturbosch.detekt.api.commaSeparatedPattern
import org.apache.maven.BuildFailureException
import org.apache.maven.project.MavenProject
import kotlin.io.path.Path


internal fun ProcessingSpecBuilder.buildLogging(checkMojo: CheckMojo) {
    logging {
        debug = checkMojo.debug
    }
}

internal fun ProcessingSpecBuilder.buildExecution(checkMojo: CheckMojo) {
    execution {
        parallelAnalysis = checkMojo.parallel
        parallelParsing = checkMojo.parallel
    }
}

internal fun ProcessingSpecBuilder.buildBaseline(checkMojo: CheckMojo) {
    baseline {
        if (checkMojo.baseline.isNotBlank())
            path = Path(checkMojo.baseline)
    }
}

internal fun ProcessingSpecBuilder.buildCompiler(checkMojo: CheckMojo) {
    compiler {
        jvmTarget = checkMojo.jvmTarget.ifBlank { jvmTarget }
        languageVersion = checkMojo.languageVersion.ifBlank { null }
        classpath = checkMojo.classPath
    }
}

internal fun ProcessingSpecBuilder.buildReports(checkMojo: CheckMojo) {
    reports {
        checkMojo.report.map {
            val parts = it.split(":")
            Pair(parts[0], parts[1])
        }.forEach {
            report { it.first to Path(it.second) }
        }
    }
}

internal fun ProcessingSpecBuilder.buildExtensions(checkMojo: CheckMojo,
                                                   mavenProject: MavenProject?,
                                                   localRepoLocation: String) {
    extensions {
        disableDefaultRuleSets = checkMojo.disableDefaultRuleSets
        val pluginPaths = checkMojo.plugins
            .buildPluginPaths(mavenProject, localRepoLocation, checkMojo.log)
            .split(SEMICOLON)
            .filter { it.isNotBlank() }
            .map { Path(it) }
        fromPaths {
            pluginPaths
        }
    }
}

internal fun ProcessingSpecBuilder.buildConfig(checkMojo: CheckMojo,
                                               mavenProject: MavenProject?) {
    config {
        configPaths = if (checkMojo.config.isNotBlank())
            listOf(Path(resolveConfig(mavenProject, checkMojo.config)))
        else emptyList()
        knownPatterns = emptyList()
        useDefaultConfig = checkMojo.buildUponDefaultConfig
        shouldValidateBeforeAnalysis = false
        resources = if (checkMojo.configResource.isNotBlank())
            listOf(javaClass.getResource(checkMojo.configResource)
                ?: throw BuildFailureException("Classpath resource '${checkMojo.configResource}' does not exist!")
            )
        else emptyList()
    }
}

internal fun ProcessingSpecBuilder.buildRules(checkMojo: CheckMojo) {
    rules {
        autoCorrect = checkMojo.autoCorrect
        @Suppress("DEPRECATION")
        activateAllRules = checkMojo.failFast || checkMojo.allRules
        maxIssuePolicy = when (val count = checkMojo.maxIssues.toIntOrNull()) {
            null -> RulesSpec.MaxIssuePolicy.NonSpecified // prefer to read from config
            0 -> RulesSpec.MaxIssuePolicy.NoneAllowed
            in -1 downTo Int.MIN_VALUE -> RulesSpec.MaxIssuePolicy.AllowAny
            else -> RulesSpec.MaxIssuePolicy.AllowAmount(count)
        }
    }
}

internal fun ProcessingSpecBuilder.buildProject(checkMojo: CheckMojo) {
    project {
        includes = asPatterns(checkMojo.includes)
        excludes = asPatterns(checkMojo.excludes)
        basePath = Path(checkMojo.basePath)
        inputPaths = listOf(Path(checkMojo.input))
    }
}

private fun asPatterns(rawValue: String?): List<String> =
    rawValue?.trim()
        ?.commaSeparatedPattern(",", ";")
        ?.toList()
        .orEmpty()
