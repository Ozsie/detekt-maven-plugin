package com.github.ozsie

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.expect

class GenerateConfigMojoSpec : Spek({
    given("a GenerateConfigMojo and 'skip' is true") {
        val generateConfigMojo = GenerateConfigMojo()
        generateConfigMojo.skip = true
        on("generateConfigMojo.execute()") {
            test("unit is expected") {
                expect(Unit) {
                    generateConfigMojo.execute()
                }
            }
        }
    }

    given("a GenerateConfigMojo and 'skip' is false") {
        val generateConfigMojo = GenerateConfigMojo()
        generateConfigMojo.skip = false
        on("generateConfigMojo.execute()") {
            test("Config file is generated") {
                generateConfigMojo.execute()
                val file = File("default-detekt-config.yml")
                assertTrue(file.exists())
                file.deleteOnExit()
            }
        }
    }

    given("a GCMojo and 'skip' is true") {
        val generateConfigMojo = GCMojo()
        generateConfigMojo.skip = true
        on("generateConfigMojo.execute()") {
            test("unit is expected") {
                expect(Unit) {
                    generateConfigMojo.execute()
                }
            }
        }
    }

    given("a GCMojo and 'skip' is false") {
        val generateConfigMojo = GCMojo()
        generateConfigMojo.skip = false
        on("generateConfigMojo.execute()") {
            test("Config file is generated") {
                generateConfigMojo.execute()
                val file = File("default-detekt-config.yml")
                assertTrue(file.exists())
                file.deleteOnExit()
            }
        }
    }
})
