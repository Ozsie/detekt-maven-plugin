package com.github.ozsie

import com.beust.jcommander.ParameterException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertFailsWith
import kotlin.test.expect

class CreateBaselineMojoSpec : Spek({
    given("a CreateBaselineMojo and 'skip' is true") {
        val createBaselineMojo = CreateBaselineMojo()
        createBaselineMojo.skip = true
        on("createBaselineMojo.execute()") {
            test("unit is expected") {
                expect(Unit) {
                    createBaselineMojo.execute()
                }
            }
        }
    }

    given("a CreateBaselineMojo and 'skip' is false") {
        val createBaselineMojo = CreateBaselineMojo()
        createBaselineMojo.skip = false
        on("createBaselineMojo.execute()") {
            test("ParameterException is thrown") {
                assertFailsWith(ParameterException::class) {
                    createBaselineMojo.execute()
                }
            }
        }
    }

    given("a CBMojo and 'skip' is true") {
        val createBaselineMojo = CBMojo()
        createBaselineMojo.skip = true
        on("createBaselineMojo.execute()") {
            test("unit is expected") {
                expect(Unit) {
                    createBaselineMojo.execute()
                }
            }
        }
    }

    given("a CBMojo and 'skip' is false") {
        val createBaselineMojo = CBMojo()
        createBaselineMojo.skip = false
        on("createBaselineMojo.execute()") {
            test("ParameterException is thrown") {
                assertFailsWith(ParameterException::class) {
                    createBaselineMojo.execute()
                }
            }
        }
    }
})
