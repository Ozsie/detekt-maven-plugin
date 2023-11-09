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
@Mojo(name = "check-with-type-resolution",
        defaultPhase = LifecyclePhase.VERIFY,
        threadSafe = true,
        requiresDependencyCollection = ResolutionScope.TEST)
open class CheckWithTypeResolutionMojo : DetektMojo() {
    lateinit var cliArgs: CliArgs

    override fun execute() {
        getCliSting().forEach {
            log.debug("Applying $it")
        }
        this.cliArgs = parseArguments(getCliSting().log().toTypedArray())
        val invalidInputs = input.split(",").filter {
            val path = Paths.get(it)
            !Files.isDirectory(Paths.get(it)) && !Files.exists(path)
        }
        if (!skip && invalidInputs.isEmpty()) {
            setDefaultClasspathIfNotSet(cliArgs)
            failBuildIfNeeded { Runner(cliArgs, System.out, System.err).execute() }
        } else
            inputSkipLog(skip, invalidInputs)
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

    private fun inputSkipLog(skip: Boolean, invalidInputDirs: List<String>) {
        if (skip) {
            log.info("Skipping execution")
        } else {
            log.info("Failed to resolve input directories '${invalidInputDirs.joinToString()}', skipping module")
        }
    }
}

@Suppress("unused") @Mojo(name = "ctr") class CTRMojo : CheckWithTypeResolutionMojo()
