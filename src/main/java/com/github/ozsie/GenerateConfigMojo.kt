package com.github.ozsie

import io.gitlab.arturbosch.detekt.cli.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.Runner
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.project.MavenProject
import org.apache.maven.plugins.annotations.*
import java.io.File

@Suppress("unused")
@Mojo(name = "generate-config")
class GenerateConfigMojo : AbstractMojo() {
    override fun execute() { ConfigExporter().execute() }
}
