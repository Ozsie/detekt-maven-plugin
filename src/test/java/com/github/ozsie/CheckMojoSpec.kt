package com.github.ozsie

import io.github.detekt.tooling.api.MaxIssuesReached
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertFailsWith
import kotlin.test.expect

class CheckMojoSpec : Spek({
    val codeSamplesDirectory = CheckMojoSpec::class.java.classLoader.getResource("code-samples")!!.file
    val invalidPackageNamingDirectoryPath = "$codeSamplesDirectory/invalid-package-naming"

    given("a CheckMojo and 'skip' is true") {
        val checkMojo = CheckMojo()
        checkMojo.skip = true
        on("checkMojo.execute()") {
            test("unit is expected") {
                expect(Unit) {
                    checkMojo.execute()
                }
            }
        }
    }

    given("a CheckMojo and 'skip' is false") {
        val checkMojo = CheckMojo()
        checkMojo.skip = false
        on("checkMojo.execute()") {
            test("Unit is expected") {
                expect(Unit) {
                    checkMojo.execute()
                }
            }
        }
    }

    given("a CheckMojo and 'failBuildOnMaxIssuesReached' is false") {
        val checkMojo = CheckMojo().apply {
            input = invalidPackageNamingDirectoryPath
            failBuildOnMaxIssuesReached = false
        }
        on("checkMojo.execute()") {
            test("Unit is expected") {
                expect(Unit) {
                    checkMojo.execute()
                }
            }
        }
    }

    given("a CheckMojo and 'failBuildOnMaxIssuesReached' is true") {
        val checkMojo = CheckMojo().apply {
            input = invalidPackageNamingDirectoryPath
            failBuildOnMaxIssuesReached = true
        }
        on("checkMojo.execute()") {
            test("Unit is expected") {
                assertFailsWith(MaxIssuesReached::class, "Build failed with 1 weighted issues.") {
                    checkMojo.execute()
                }
            }
        }
    }

    given("multiple valid comma separated input directories are supplied") {
        val checkMojo = CheckMojo().apply {
            input = "$codeSamplesDirectory/valid,$codeSamplesDirectory/valid2"
            failBuildOnMaxIssuesReached = true
        }
        on("checkMojo.execute()") {
            test("detekt analyses the specified directories") {
                assertFailsWith(MaxIssuesReached::class, "Build failed with 2 weighted issues.") {
                    checkMojo.execute()
                }
            }
        }
    }

    given("a mix of valid and invalid comma separated input directories are supplied") {
        val checkMojo = CheckMojo().apply {
            input = "$codeSamplesDirectory/valid,invalidDirectory"
            failBuildOnMaxIssuesReached = false
        }
        on("checkMojo.execute()") {
            test("detekt analysis is aborted") {
                expect(Unit) {
                    checkMojo.execute()
                }
            }
        }
    }
})
