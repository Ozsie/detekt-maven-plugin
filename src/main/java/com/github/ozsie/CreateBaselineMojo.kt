package com.github.ozsie

import io.github.detekt.tooling.api.DetektProvider
import io.github.detekt.tooling.api.spec.ProcessingSpec
import org.apache.maven.plugins.annotations.Mojo
import kotlin.io.path.Path

@Suppress("unused")
@Mojo(name = "create-baseline")
open class CreateBaselineMojo : DetektMojo() {
    override fun execute() {
        if (!skip) {
            buildProcessingSpec().also { spec ->
                DetektProvider.load().get(spec).run()
            }
        }
    }

    private fun buildProcessingSpec() = ProcessingSpec {
        project {
            this.inputPaths = listOf(Path(input))
        }
        baseline {
            path = if (baseline.isBlank()) Path("baseline-${mavenProject?.name}.xml") else Path(baseline)
            log.info("Creating baseline file for ${mavenProject?.name}: ${path?.fileName}")
            shouldCreateDuringAnalysis = true
        }
    }
}

@Suppress("unused") @Mojo(name = "cb") class CBMojo : CreateBaselineMojo()
