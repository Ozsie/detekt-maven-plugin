package com.github.ozsie

import io.github.detekt.tooling.api.MaxIssuesReached
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertFailsWith
import kotlin.test.expect

class CheckMojoSpec : Spek({
    val invalidPackageNamingDirectoryPath by lazy {
        CheckMojoSpec::class.java.classLoader.getResource("code-samples/invalid-package-naming")!!
            .file
    }

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
})
