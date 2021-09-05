package com.github.ozsie

import io.github.detekt.tooling.api.DetektProvider
import io.github.detekt.tooling.api.MaxIssuesReached
import io.github.detekt.tooling.api.spec.ProcessingSpec
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope
import java.nio.file.Files
import java.nio.file.Paths

@Suppress("unused")
@Mojo(name = "check",
        defaultPhase = LifecyclePhase.VERIFY,
        threadSafe = true,
        requiresDependencyCollection = ResolutionScope.TEST)
class CheckMojo : DetektMojo() {
    override fun execute() {
        val foundInputDir = Files.isDirectory(Paths.get(input))
        if (!skip && foundInputDir)
            failBuildIfNeeded {
                buildProcessingSpec().also { spec ->
                    val run = DetektProvider.load().get(spec).run()
                    if (run.error != null) {
                        throw run.error!!
                    }
                }
            }
        else
            inputSkipLog(skip)
    }

    private fun failBuildIfNeeded(block: () -> Unit) {
        try {
            block()
        } catch (e: MaxIssuesReached) {
            if (failBuildOnMaxIssuesReached)
                throw e
            else
                log.warn("Detekt check found issues. Ignoring because 'failBuildOnMaxIssuesReached' is set to false")
        }
    }

    private fun buildProcessingSpec() = ProcessingSpec {
        buildLogging(this@CheckMojo)
        buildExecution(this@CheckMojo)
        buildBaseline(this@CheckMojo)
        buildProject(this@CheckMojo)
        buildRules(this@CheckMojo)
        buildConfig(this@CheckMojo, mavenProject)
        buildExtensions(this@CheckMojo, mavenProject, localRepoLocation)
        buildReports(this@CheckMojo)
        buildCompiler(this@CheckMojo)
    }
}
