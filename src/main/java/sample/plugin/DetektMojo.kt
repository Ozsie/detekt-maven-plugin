package sample.plugin

import io.gitlab.arturbosch.detekt.cli.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.Runner
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject


@Mojo(name = "detekt", defaultPhase = LifecyclePhase.VERIFY,
        requiresDependencyCollection = ResolutionScope.TEST,
        requiresDependencyResolution = ResolutionScope.TEST)
class DetektMojo : AbstractMojo() {
    @Parameter(property = "detekt.baseline", defaultValue = "")
    var baseline = ""

    @Parameter(property = "detekt.config", defaultValue = "")
    var config: String = ""

    @Parameter(property = "detekt.config-resource", defaultValue = "")
    var configResource = ""

    @Parameter(property = "detekt.create-baseline", defaultValue = "false")
    var createBaseline = false

    @Parameter(property = "detekt.debug", defaultValue = "false")
    var debug = false

    @Parameter(property = "detekt.disable-default-rulesets", defaultValue = "false")
    var disableDefaultRulesets = false

    @Parameter(property = "detekt.filters")
    var filters = ArrayList<String>()

    @Parameter(property = "detekt.generate-config", defaultValue = "false")
    var generateConfig = false

    @Parameter(property = "detekt.help", defaultValue = "false")
    var help = false

    @Parameter(property = "detekt.input", defaultValue = "\${basedir}/src")
    var input = "\${basedir}/src"

    @Parameter(property = "detekt.output", defaultValue = "\${basedir}/detekt")
    var output = "\${basedir}/detekt"

    @Parameter(property = "detekt.output-name", defaultValue = "")
    var outputName = ""

    @Parameter(property = "detekt.parallel", defaultValue = "false")
    var parallel = false

    @Parameter(property = "detekt.plugins")
    var plugins = ArrayList<String>()

    @Parameter(defaultValue = "\${project}", readonly = true)
    var mavenProject: MavenProject? = null

    override fun execute() {
        val arguments = parseArguments(buildCLIString())
        val executable = when {
            arguments.generateConfig -> ConfigExporter()
            else -> Runner(arguments)
        }
        executable.execute()
    }

    private fun buildCLIString(): Array<String> {
        val parameters = ArrayList<String>()
        if (help) {
            parameters.add("-h")
        }
        if (baseline.isNotEmpty()) {
            parameters.add("-b")
            parameters.add(baseline)
        }
        if (config.isNotEmpty()) {
            parameters.add("-c")
            parameters.add(config)
        }
        if (configResource.isNotEmpty()) {
            parameters.add("-cr")
            parameters.add(configResource)
        }
        if (createBaseline) {
            parameters.add("-cb")
        }
        if (debug) {
            parameters.add("--debug")
        }
        if (disableDefaultRulesets) {
            parameters.add("-dd")
        }
        if (!filters.isEmpty()) {
            parameters.add("-f")
            parameters.add(filters.joinToString(";"))
        }
        if (generateConfig) {
            parameters.add("-gc")
        }
        if (input.isNotEmpty()) {
            parameters.add("-i")
            parameters.add(input)
        }
        if (output.isNotEmpty()) {
            parameters.add("-o")
            parameters.add(output)
        }
        if (outputName.isNotEmpty()) {
            parameters.add("-on")
            parameters.add(outputName)
        }
        if (parallel) {
            parameters.add("-parallel")
        }
        if (!plugins.isEmpty()) {
            parameters.add("-p")
            plugins.forEach { plugin ->
                val artifacts = mavenProject?.artifacts?.filter {
                    "${it.groupId}:${it.artifactId}" == plugin
                }

                if (artifacts != null && artifacts.isNotEmpty()) {
                    parameters.add(artifacts.first().file.absolutePath)
                }
            }
        }

        return parameters.toTypedArray()
    }
}
