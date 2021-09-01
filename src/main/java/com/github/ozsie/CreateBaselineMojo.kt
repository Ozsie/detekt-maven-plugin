package com.github.ozsie

import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import org.apache.maven.plugins.annotations.Mojo
import java.io.File

@Suppress("unused")
@Mojo(name = "create-baseline")
open class CreateBaselineMojo : DetektMojo() {
    override fun execute() {
        val cliStr = cliString
        cliStr.forEach {
            log.debug("Applying $it")
        }
        val srcExists = cliStr.filter { it.endsWith("/src") }.any { File(it).exists() }
        val cliArgs = parseArguments(cliStr)
        if (!srcExists) {
            log.info("Skipping module without 'src' directory")
        } else if (!skip) Runner(cliArgs, System.out, System.err).execute()
    }
    private val cliString get() = getCliSting().apply {
        add(CREATE_BASELINE)
        if (!contains(BASELINE)) {
            log.debug("Baseline flag not present for ${mavenProject?.name}")
            add(BASELINE)
            add("baseline-${mavenProject?.name}.xml")
        }
    }.log().toTypedArray()
}

@Suppress("unused") @Mojo(name = "cb") class CBMojo : CreateBaselineMojo()
