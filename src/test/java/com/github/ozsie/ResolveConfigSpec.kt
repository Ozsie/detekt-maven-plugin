package com.github.ozsie

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// Note: while annoying, the use of File() in the asserts should maximize cross-platform compatibility
object ResolveConfigSpec : Spek({

    given("a test project") {
        val project = projectWithBasedirAt("resolve-config")
        val basedir = project.basedir
        given("a relative config name") {
            val config = "one.yml"
            on("resolveConfig") {
                val result = resolveConfig(project, config)
                test("resolves the file name to an absolute path") {
                    assertEquals(
                        File(basedir, "one.yml").absolutePath,
                        result
                    )
                }
            }
        }
        given("an absolute config name") {
            val config = resolveTestResourcePath("resolve-config/nested/three.yml").absolutePath
            on("resolveConfig") {
                val result = resolveConfig(project, config)
                test("returns the input") {
                    assertEquals(config, result)
                }
            }
        }
        given("semicolon-separated config names") {
            val config = "one.yml;two.yml"
            on("resolveConfig") {
                val result = resolveConfig(project, config)
                test("resolves all config files") {
                    assertEquals(
                        "${File(basedir, "one.yml")};${File(basedir, "two.yml")}",
                        result
                    )
                }
            }
        }
        given("comma-separated config names") {
            val config = "one.yml,two.yml"
            on("resolveConfig") {
                val result = resolveConfig(project, config)
                test("resolves all config files") {
                    assertEquals(
                        "${File(basedir, "one.yml")};${File(basedir, "two.yml")}",
                        result
                    )
                }
            }
        }
        given("a non-existent path") {
            val config = "fake.yml"
            on("resolveConfig") {
                test("FileNotFound exception is thrown") {
                    assertFailsWith<FileNotFoundException> { resolveConfig(project, config) }
                }
            }
        }
    }

    given("a nested project") {
        val project = projectWithBasedirAt("resolve-config/nested")
        val basedir = project.basedir
        val parentDir = basedir.parentFile

        given("a file in the parent project") {
            val config = "one.yml"
            on("resolveConfig") {
                val result = resolveConfig(project, config)
                test("resolves to the parent directory") {
                    assertEquals(
                        File(parentDir, "one.yml").absolutePath,
                        result
                    )
                }
            }
        }
        given("multiple config names") {
            val config = "one.yml;three.yml"
            on("resolveConfig") {
                val result = resolveConfig(project, config)
                test("resolves resolves files in parent and child") {
                    assertEquals(
                        "${File(parentDir, "one.yml")};${File(basedir, "three.yml")}",
                        result
                    )
                }
            }
        }
    }

    given("remote config file") {
        val project = projectWithBasedirAt("resolve-config")

        given("remote file exists") {
            val config = "https://raw.githubusercontent.com/yonbav/detekt-maven-plugin/master/src/test/resources/resolve-config/remote/remote-config.yml"
            on("resolveConfig") {
                val result = resolveConfig(project, config)
                test("resolves the remote file") {
                    assertEquals(EXPORTED_FILE_LOCATION, result)
                    assertTrue(Files.exists(Paths.get(project.basedir.absolutePath + EXPORTED_FILE_LOCATION)))
                }
            }
        }

        given("remote file does not exist") {
            val config = "https://raw.githubusercontent.com/yonbav/detekt-maven-plugin/master/src/test/resources/resolve-config/remote/not-exist-config.yml"
            on("resolveConfig") {
                val exception = assertFailsWith<FileNotFoundException> {
                    resolveConfig(project, config)
                }
                test("resolves the remote file") {
                    assertEquals(config, exception.message)
                }
            }
        }
    }
})
