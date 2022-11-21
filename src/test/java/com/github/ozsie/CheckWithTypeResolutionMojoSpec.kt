package com.github.ozsie

import com.github.ozsie.test.CheckWithTypeResolutionMojoTestFactory
import io.github.detekt.tooling.api.MaxIssuesReached
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertFailsWith
import kotlin.test.expect

class CheckWithTypeResolutionMojoSpec : Spek({
    given("a CheckMojo and 'skip' is true") {
        val checkMojo = CheckWithTypeResolutionMojo()
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
        val checkMojo = CheckWithTypeResolutionMojo()
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
        val checkMojo = CheckWithTypeResolutionMojoTestFactory.createWithInvalidPackageNamingStructure {
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
        val checkMojo = CheckWithTypeResolutionMojoTestFactory.createWithInvalidPackageNamingStructure {
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

    given("classpath parameter") {
        val checkMojo = CheckWithTypeResolutionMojoTestFactory.createWithNoRuleExecution {
            classPath = "/tmp/provided"
        }

        on("checkMojo.execute()") {
            test("uses provider value") {
                expect("/tmp/provided") {
                    checkMojo.execute()
                    checkMojo.cliArgs.classpath
                }
            }
        }
    }

    given("no classpath parameter") {
        val checkMojo = CheckWithTypeResolutionMojoTestFactory.createWithNoRuleExecution()

        on("checkMojo.execute()") {
            test("uses default compileClasspathElements") {
                expect("/tmp/default${java.io.File.pathSeparatorChar}/tmp/default2") {
                    checkMojo.execute()
                    checkMojo.cliArgs.classpath
                }
            }
        }
    }
})
