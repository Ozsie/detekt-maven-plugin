package com.github.ozsie

import io.github.detekt.tooling.api.MaxIssuesReached
import kotlin.test.assertFailsWith
import kotlin.test.expect
import org.junit.jupiter.api.Test

class CheckMojoTest {
    private val codeSamplesDirectory = CheckMojoTest::class.java.classLoader.getResource("code-samples")!!.file
    private val invalidPackageNamingDirectoryPath = "$codeSamplesDirectory/invalid-package-naming"

    @Test
    fun `a CheckMojo and 'skip' is true Unit is expected`() {
        val checkMojo = CheckMojo()
        checkMojo.skip = true
        expect(Unit) {
            checkMojo.execute()
        }
    }

    @Test
    fun `a CheckMojo and 'skip' is false Unit is expected`()  {
        val checkMojo = CheckMojo()
        checkMojo.skip = false
        expect(Unit) {
            checkMojo.execute()
        }
    }

    @Test
    fun `a CheckMojo and 'failBuildOnMaxIssuesReached' is false Unit is exptected`()  {
        val checkMojo = CheckMojo().apply {
            input = invalidPackageNamingDirectoryPath
            failBuildOnMaxIssuesReached = false
        }
        expect(Unit) {
            checkMojo.execute()
        }
    }

    @Test
    fun `a CheckMojo and 'failBuildOnMaxIssuesReached' is true Unit is exptected`()  {
        val checkMojo = CheckMojo().apply {
            input = invalidPackageNamingDirectoryPath
            failBuildOnMaxIssuesReached = true
        }
        assertFailsWith(MaxIssuesReached::class, "Build failed with 1 weighted issues.") {
            checkMojo.execute()
        }
    }

    @Test
    fun `multiple valid comma separated input directories are supplied detekt analyses the specified directories`()  {
        val checkMojo = CheckMojo().apply {
            input = "$codeSamplesDirectory/valid,$codeSamplesDirectory/valid2"
            failBuildOnMaxIssuesReached = true
        }
        assertFailsWith(MaxIssuesReached::class, "Build failed with 2 weighted issues.") {
            checkMojo.execute()
        }
    }

    @Test
    fun `a mix of valid and invalid comma separated input directories are supplied detekt analysis is aborted`()  {
        val checkMojo = CheckMojo().apply {
            input = "$codeSamplesDirectory/valid,invalidDirectory"
            failBuildOnMaxIssuesReached = false
        }
        expect(Unit) {
            checkMojo.execute()
        }
    }
}
