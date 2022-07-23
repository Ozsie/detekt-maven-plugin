package com.github.ozsie

import io.github.detekt.tooling.api.MaxIssuesReached
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope
import java.nio.file.Files
import java.nio.file.Paths

@Suppress("unused")
@Mojo(name = "check",
        defaultPhase = LifecyclePhase.VERIFY,
        threadSafe = true,
        requiresDependencyCollection = ResolutionScope.TEST)
class CheckMojo : DetektMojo() {
    lateinit var cliArgs: CliArgs

    override fun execute() {
        getCliSting().forEach {
            log.debug("Applying $it")
        }
        this.cliArgs = parseArguments(getCliSting().log().toTypedArray())

        val foundInputDir = Files.isDirectory(Paths.get(input))
        if (!skip && foundInputDir) {
            setDefaultClasspathIfNotSet(cliArgs)
            failBuildIfNeeded { Runner(cliArgs, System.out, System.err).execute() }
        } else
            inputSkipLog(skip)
    }

    private fun setDefaultClasspathIfNotSet(cliArgs: CliArgs) {
        if (cliArgs.classpath.isNullOrBlank()) {
            cliArgs.classpath = mavenProject?.compileClasspathElements?.joinToString(
                java.io.File.pathSeparatorChar.toString())
        }
    }

    private fun failBuildIfNeeded(block: () -> Unit) {
        try {
            block()
        } catch (e: MaxIssuesReached) {
            if (failBuildOnMaxIssuesReached)
                throw e
            else
                log.warn("Detekt check found issues. Ignoring because 'failBuildOnMaxIssuesReached' is set to false")
        }
    }

    private fun inputSkipLog(skip: Boolean) {
        if (skip) log.info("Skipping execution") else log.info("Input directory '$input' not found, skipping module")
    }
}
