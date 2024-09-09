package com.github.ozsie

import com.github.ozsie.test.CheckWithTypeResolutionMojoTestFactory
import io.github.detekt.tooling.api.MaxIssuesReached
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.expect

class CheckWithTypeResolutionMojoTest {
    @Test
    fun `a CheckMojo and 'skip' is true`()  {
        val checkMojo = CheckWithTypeResolutionMojo()
        checkMojo.skip = true
        expect(Unit) {
            checkMojo.execute()
        }
    }

    @Test
    fun `a CheckMojo and 'skip' is false`()  {
        val checkMojo = CheckWithTypeResolutionMojo()
        checkMojo.skip = false
        expect(Unit) {
            checkMojo.execute()
        }
    }

    @Test
    fun `a CheckMojo and 'failBuildOnMaxIssuesReached' is false`()  {
        val checkMojo = CheckWithTypeResolutionMojoTestFactory.createWithInvalidPackageNamingStructure {
            failBuildOnMaxIssuesReached = false
        }

        expect(Unit) {
            checkMojo.execute()
        }
    }

    @Test
    fun `a CheckMojo and 'failBuildOnMaxIssuesReached' is true`()  {
        val checkMojo = CheckWithTypeResolutionMojoTestFactory.createWithInvalidPackageNamingStructure {
            failBuildOnMaxIssuesReached = true
        }
        assertFailsWith(MaxIssuesReached::class, "Build failed with 1 weighted issues.") {
            checkMojo.execute()
        }
    }

    @Test
    fun `classpath parameter`()  {
        val checkMojo = CheckWithTypeResolutionMojoTestFactory.createWithNoRuleExecution {
            classPath = "/tmp/provided"
        }

        expect("/tmp/provided") {
            checkMojo.execute()
            checkMojo.cliArgs.classpath
        }
    }

    @Test
    fun `no classpath parameter`()  {
        val checkMojo = CheckWithTypeResolutionMojoTestFactory.createWithNoRuleExecution()

        expect("/tmp/default${java.io.File.pathSeparatorChar}/tmp/default2") {
            checkMojo.execute()
            checkMojo.cliArgs.classpath
        }
    }
}
