package com.github.ozsie

import io.github.detekt.tooling.api.DefaultConfigurationProvider
import org.apache.maven.plugins.annotations.Mojo
import java.nio.file.Paths

@Suppress("unused")
@Mojo(name = "generate-config")
open class GenerateConfigMojo : DetektMojo() {
    override fun execute() {
        val path = if (this.config.isBlank()) "detekt.yml" else this.config
        val configPath = Paths.get(path)
        DefaultConfigurationProvider.load().copy(configPath)
    }
}

@Suppress("unused") @Mojo(name = "gc") class GCMojo : GenerateConfigMojo()
