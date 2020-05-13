package com.github.ozsie

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import kotlin.test.expect

class CheckMojoSpec : Spek({
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
})
