package com.github.ozsie

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.apache.maven.model.Dependency
import org.apache.maven.model.Plugin
import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugin.logging.SystemStreamLog
import org.apache.maven.project.MavenProject
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

object DetektMojoSpec : Spek({
    given("an ArrayList") {
        val arrayList = arrayListOf("a", "b", "c")

        on("useIf") {
            arrayList.useIf(true, "d")
            test("length should match") {
                assertEquals(4, arrayList.size)
            }
        }
    }

    given("a StringBuilder") {
        val sb = StringBuilder()

        val plugin = Plugin().apply {
            dependencies = mutableListOf(Dependency().apply {
                groupId = "x.y"
                artifactId = "y"
                version = "1"
            })
        }

        on("buildPluginPaths") {
            sb.buildPluginPaths(arrayListOf("x.y:y"), plugin, "~")
            test("sb should contain path to plugin") {
                assertEquals("~/x/y/y/1/y-1.jar;", sb.toString())
            }
        }
    }

    given("a Dependency") {
        val dependency = Dependency().apply {
            groupId = "x.y"
            artifactId = "z"
            version = "1"
        }

        on("asPath") {
            val path = dependency asPath "~"
            test("path should equal path to plugin") {
                assertEquals("~/x/y/z/1/z-1.jar", path)
            }
        }

        on("getIdentifier") {
            val identifier = dependency.getIdentifier()
            test("identifier should equal groupId:artifactId") {
                assertEquals("x.y:z", identifier)
            }
        }
    }

    given("a String") {
        val groupId = "x.y.z"
        on("asPath") {
            val path = groupId.asPath()
            test(". should be replaced by / in path") {
                assertEquals("x/y/z", path)
            }
        }
    }

    given("an ArrayList<String>") {
        val mavenProject: MavenProject = mock {
            on {
                getPlugin(any())
            } doReturn (
                Plugin().apply {
                    dependencies = mutableListOf(Dependency().apply {
                        groupId = "a.b"
                        artifactId = "b"
                        version = "1"
                    })
                }
            )
        }

        val stringList = arrayListOf("x.y:y", "a.b:b", "c.d:d")
        on("buildPluginPaths") {
            val pluginPath = stringList.buildPluginPaths(mavenProject, "~/.m2", SystemStreamLog())
            test("StringBuilder.buildPLuginPaths should be called") {
                assertEquals("~/.m2/a/b/b/1/b-1.jar", pluginPath)
            }
        }
    }

    given("an ArrayList<T>") {
        val log: Log = mock {}
        val stringList = arrayListOf("x.y:y", "a.b:b", "c.d:d")
        on("log") {
            val listAfterLog = stringList.log(log)
            test("list should not be modified") {
                assertEquals(stringList, listAfterLog)
            }
        }
    }
})
