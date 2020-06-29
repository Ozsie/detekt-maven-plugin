package com.github.ozsie

import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import org.apache.maven.plugins.annotations.Mojo

@Suppress("unused")
@Mojo(name = "generate-config")
open class GenerateConfigMojo : DetektMojo() {
    override fun execute() {
        val cliArgs = parseArguments(getCliSting().log().toTypedArray(), System.out, System.err)
        ConfigExporter(cliArgs).run {
            if (!skip) return execute()
        }
    }
}

@Suppress("unused") @Mojo(name = "gc") class GCMojo : GenerateConfigMojo()
