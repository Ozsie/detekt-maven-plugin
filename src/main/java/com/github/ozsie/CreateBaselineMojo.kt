package com.github.ozsie

import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import org.apache.maven.plugins.annotations.Mojo

@Suppress("unused")
@Mojo(name = "create-baseline")
open class CreateBaselineMojo : DetektMojo() {
    override fun execute() {
        val cliStr = cliString
        cliStr.forEach {
            log.debug("Applying $it")
        }
        val cliArgs = parseArguments(cliStr, System.out, System.err)
        if (!skip) Runner(cliArgs, System.out, System.err).execute()
    }
    private val cliString get() = getCliSting().apply {
        add(CREATE_BASELINE)
        if (!contains(BASELINE)) {
            log.debug("Baseline flag not present")
            add(BASELINE)
            add("baseline.xml")
        }
    }.log().toTypedArray()
}

@Suppress("unused") @Mojo(name = "cb") class CBMojo : CreateBaselineMojo()
