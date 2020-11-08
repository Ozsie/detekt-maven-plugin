package com.github.ozsie

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
    override fun execute() {
        getCliSting().forEach {
            log.debug("Applying $it")
        }
        val cliArgs = parseArguments(getCliSting().log().toTypedArray())
        val foundInputDir = Files.isDirectory(Paths.get(input))
        if (!skip && foundInputDir) return Runner(cliArgs, System.out, System.err).execute() else inputSkipLog(skip)
    }

    private fun inputSkipLog(skip: Boolean) {
        if (skip) log.info("Skipping execution") else log.info("Input directory '$input' not found, skipping module")
    }
}
