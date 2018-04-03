package com.github.ozsie

import io.gitlab.arturbosch.detekt.cli.Runner
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.apache.maven.plugins.annotations.Mojo

@Suppress("unused")
@Mojo(name = "create-baseline")
open class CreateBaselineMojo : DetektMojo() {

    override fun execute() = Runner(parseArguments(cliString)).execute()

    private val cliString
        get() = ArrayList<String>().apply {
                useIf(debug, DEBUG)
                    .useIf(disableDefaultRuleSets, DISABLE_DEFAULT_RULE_SET)
                    .useIf(parallel, PARALLEL)
                    .useIf(baseline.isNotEmpty(), BASELINE, baseline)
                    .useIf(config.isNotEmpty(), CONFIG, config)
                    .useIf(configResource.isNotEmpty(), CONFIG_RESOURCE, configResource)
                    .useIf(filters.isNotEmpty(), FILTERS, filters.joinToString(";"))
                    .useIf(input.isNotEmpty(), INPUT, input)
                    .useIf(output.isNotEmpty(), OUTPUT, output)
                    .useIf(outputName.isNotEmpty(), OUTPUT_NAME, outputName)
                    .useIf(plugins.isNotEmpty(), PLUGINS, plugins.buildPluginPaths(mavenProject, localRepoLocation))
                    .add(CREATE_BASELINE)
            }.log().toTypedArray()
}

@Suppress("unused")
@Mojo(name = "cb")
class CBMojo : CreateBaselineMojo()
