package com.github.ozsie

import io.gitlab.arturbosch.detekt.cli.Runner
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.apache.maven.plugins.annotations.Mojo



@Suppress("unused")
@Mojo(name = "create-baseline")
open class CreateBaselineMojo : DetektMojo() {
    override fun execute() = Runner(parseArguments(cliString)).execute()
    private val cliString get() = getCliSting().apply { add(CREATE_BASELINE) }.log().toTypedArray()
}

@Suppress("unused") @Mojo(name = "cb") class CBMojo : CreateBaselineMojo()
