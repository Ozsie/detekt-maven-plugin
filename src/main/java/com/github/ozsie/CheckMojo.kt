package com.github.ozsie

import io.gitlab.arturbosch.detekt.cli.parseArguments
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope

@Suppress("unused")
@Mojo(name = "check", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyCollection = ResolutionScope.TEST, requiresDependencyResolution = ResolutionScope.TEST)
class CheckMojo : DetektMojo() {
    override fun execute() = Runner(parseArguments(getCliSting().log().toTypedArray())).execute()
}
