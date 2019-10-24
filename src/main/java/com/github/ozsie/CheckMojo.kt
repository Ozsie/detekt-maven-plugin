package com.github.ozsie

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
        requiresDependencyCollection = ResolutionScope.TEST)
class CheckMojo : DetektMojo() {
    override fun execute() {
        getCliSting().forEach {
            log.debug("Applying $it")
        }
        val cliArgs = parseArguments<CliArgs>(getCliSting().log().toTypedArray()).first
        skip = !Files.isDirectory(Paths.get(input))
        if (!skip) return Runner(cliArgs).execute() else log.info("Input directory '$input' not found, skipping module")
    }
}
