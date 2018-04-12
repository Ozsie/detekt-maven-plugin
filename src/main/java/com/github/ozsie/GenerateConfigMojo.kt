package com.github.ozsie

import io.gitlab.arturbosch.detekt.cli.ConfigExporter
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo

@Suppress("unused")
@Mojo(name = "generate-config")
open class GenerateConfigMojo : AbstractMojo() {
    override fun execute() = ConfigExporter().execute()
}

@Suppress("unused") @Mojo(name = "gc") class GCMojo : GenerateConfigMojo()
