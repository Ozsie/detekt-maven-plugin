package sample.plugin

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.util.concurrent.TimeUnit

const val defaultDetektVersion = "1.0.0.RC6-4"
const val defaultKotlinVersion = "1.2.30"
const val defaultJCommanderVersion = "1.72"
const val defaultSnakeyaml = "1.20"

@Mojo(name="detekt")
class DetektMojo : AbstractMojo() {

    @Parameter(property = "detekt.pluginLocation", defaultValue = "\${settings.localRepository}")
    var pluginLocation = "\${settings.localRepository}"

    @Parameter(property = "detekt.detektVersion", defaultValue = defaultDetektVersion)
    var detektVersion = defaultDetektVersion

    @Parameter(property = "detekt.detektJarLocation", defaultValue = "\${settings.localRepository}/io/gitlab/arturbosch/detekt/")
    var detektJarLocation = "\${settings.localRepository}/io/gitlab/arturbosch/detekt/"

    @Parameter(property = "detekt.kotlinVersion", defaultValue = defaultKotlinVersion)
    var kotlinVersion = defaultKotlinVersion

    @Parameter(property = "detekt.jCommanderVersion", defaultValue = defaultJCommanderVersion)
    var jCommanderVersion = defaultJCommanderVersion

    @Parameter(property = "detekt.snakeyamlVersion", defaultValue = defaultSnakeyaml)
    var snakeyamlVersion = defaultSnakeyaml

    /*
    If a baseline xml file is passed in, only new code smells not in the
      baseline are printed in the console.
     */
    @Parameter(property = "detekt.baseline", defaultValue = "")
    var baseline = ""

    /*
    Path to the config file (path/to/config.yml).
    */
    @Parameter(property = "detekt.config", defaultValue = "")
    var config: String = ""

    /*
    Path to the config resource on detekt's classpath (path/to/config.yml).
    */
    @Parameter(property = "detekt.config-resource", defaultValue = "")
    var configResource = ""

    /*
    Treats current analysis findings as a smell baseline for future detekt
    runs.
    Default: false
    */
    @Parameter(property = "detekt.create-baseline", defaultValue = "false")
    var createBaseline = false

    /*
    Debugs given ktFile by printing its elements.
    Default: false
    */
    @Parameter(property = "detekt.debug", defaultValue = "false")
    var debug = false

    /*
    Disables default rule sets.
    Default: false
    */
    @Parameter(property = "detekt.disable-default-rulesets", defaultValue = "false")
    var disableDefaultRulesets = false

    /*
    Path filters defined through regex.
     */
    @Parameter(property = "detekt.filters")
    var filters = ArrayList<String>()

    /*
    Export default config to default-detekt-config.yml.
    Default: false
    */
    @Parameter(property = "detekt.generate-config", defaultValue = "false")
    var generateConfig = false

    /*
    Shows the usage.
    */
    @Parameter(property = "detekt.help", defaultValue = "false")
    var help = false

    /*
    Input path to analyze (path/to/project).
    */
    @Parameter(property = "detekt.input", defaultValue = "\${basedir}/src")
    var input = "\${basedir}/src"

    /*
    Directory where output reports are stored.
    */
    @Parameter(property = "detekt.output", defaultValue = "\${basedir}/detekt")
    var output = "\${basedir}/detekt"

    /*
    The base name for output reports is derived from this parameter.
    */
    @Parameter(property = "detekt.output-name", defaultValue = "")
    var outputName = ""

    /*
    Enables parallel compilation of source files. Should only be used if the
    analyzing project has more than ~200 kotlin files.
    Default: false
    */
    @Parameter(property = "detekt.parallel", defaultValue = "false")
    var parallel = false

    /*
    Extra paths to plugin jars.
     */
    @Parameter(property = "detekt.plugins")
    var plugins = ArrayList<Plugin>()

    override fun execute() {
        val kotlinLocation = "/home/osdj00/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib/$kotlinVersion/kotlin-stdlib-$kotlinVersion.jar"
        val kotlinCompilerEmbeddableLocation = "/home/osdj00/.m2/repository/org/jetbrains/kotlin/kotlin-compiler-embeddable/$kotlinVersion/kotlin-compiler-embeddable-$kotlinVersion.jar"
        val kotlinScriptLocation = "/home/osdj00/.m2/repository/org/jetbrains/kotlin/kotlin-script-runtime/$kotlinVersion/kotlin-script-runtime-$kotlinVersion.jar"
        val jCommanderLocation = "/home/osdj00/.m2/repository/com/beust/jcommander/$jCommanderVersion/jcommander-$jCommanderVersion.jar"
        val snakeYamlLocation = "/home/osdj00/.m2/repository/org/yaml/snakeyaml/$snakeyamlVersion/snakeyaml-$snakeyamlVersion.jar"
        val detektCoreLocation = "${detektJarLocation}detekt-core/$detektVersion/detekt-core-$detektVersion.jar"
        val detektApiLocation = "${detektJarLocation}detekt-api/$detektVersion/detekt-api-$detektVersion.jar"

        val kotlinDependencies = "$kotlinScriptLocation:$kotlinCompilerEmbeddableLocation:$kotlinLocation:"
        val detektDependencies = "$jCommanderLocation:$detektCoreLocation:$detektApiLocation:$snakeYamlLocation:"

        val parameters: MutableList<String> = ArrayList()
        parameters.add("java")
        parameters.add("-cp")
        parameters.add("$kotlinDependencies$detektDependencies${detektJarLocation}detekt-cli/$detektVersion/detekt-cli-$detektVersion.jar")
        parameters.add("io.gitlab.arturbosch.detekt.cli.Main")

        if (help) {
            parameters.add("-h")
        } else {
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
                val sb = StringBuilder()
                filters.forEach {
                    sb.append(it).append(";")
                }
                parameters.add(sb.toString())
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
                val sb = StringBuilder()
                plugins.forEach {
                    log.info("Using plugin: $it")
                    sb.append("$pluginLocation/$it;")
                }
                parameters.add(sb.toString().removeSuffix(";"))
            }
        }

        log.info("Config: $config")

        log.info(parameters.toString())

        ProcessBuilder(parameters)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor(60, TimeUnit.MINUTES)
    }
}