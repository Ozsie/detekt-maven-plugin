package com.github.ozsie

import io.gitlab.arturbosch.detekt.cli.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.Runner
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.project.MavenProject
import org.apache.maven.plugins.annotations.*
import java.io.File

@Suppress("unused")
@Mojo(name = "create-baseline")
class CreateBaselineMojo : AbstractMojo() {
    @Parameter(property = "detekt.baseline", defaultValue = "")
    private var baseline = ""

    @Parameter(property = "detekt.config", defaultValue = "")
    private var config: String = ""

    @Parameter(property = "detekt.config-resource", defaultValue = "")
    private var configResource = ""

    @Parameter(property = "detekt.debug", defaultValue = "false")
    private var debug = false

    @Parameter(property = "detekt.disable-default-rulesets", defaultValue = "false")
    private var disableDefaultRuleSets = false

    @Parameter(property = "detekt.filters")
    private var filters = ArrayList<String>()

    @Parameter(property = "detekt.help", defaultValue = "false")
    private var help = false

    @Parameter(property = "detekt.input", defaultValue = "\${basedir}/src")
    private var input = "\${basedir}/src"

    @Parameter(property = "detekt.output", defaultValue = "\${basedir}/detekt")
    private var output = "\${basedir}/detekt"

    @Parameter(property = "detekt.output-name", defaultValue = "")
    private var outputName = ""

    @Parameter(property = "detekt.parallel", defaultValue = "false")
    private var parallel = false

    @Parameter(property = "detekt.plugins")
    private var plugins = ArrayList<String>()

    @Parameter(defaultValue = "\${project}", readonly = true)
    private var mavenProject: MavenProject? = null

    @Parameter(defaultValue = "\${settings.localRepository}", readonly = true)
    private var localRepoLocation = "\${settings.localRepository}"

    override fun execute() {
        val arguments = parseArguments(buildCLIString())
        when {
            arguments.generateConfig -> ConfigExporter()
            else -> Runner(arguments)
        }.execute()
    }

    private fun buildCLIString() = ArrayList<String>().apply {
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
                .useIf(plugins.isNotEmpty(), PLUGINS, buildPluginPaths(mavenProject, localRepoLocation, plugins))
                .add(CREATE_BASELINE)
    }.also { log.info("Args: $it") }.toTypedArray()
}
