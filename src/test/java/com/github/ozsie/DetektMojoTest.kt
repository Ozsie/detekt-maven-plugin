package com.github.ozsie

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Test
import org.apache.maven.model.Dependency
import org.apache.maven.model.Plugin
import org.apache.maven.plugin.logging.Log
import org.apache.maven.plugin.logging.SystemStreamLog
import org.apache.maven.project.MavenProject
import kotlin.test.assertEquals

object DetektMojoTest {
    @Test
    fun `an ArrayList useIf length should match`()  {
        val arrayList = arrayListOf("a", "b", "c")
        arrayList.useIf(true, "d")
        assertEquals(4, arrayList.size)
    }

    @Test
    fun `a StringBuilder buildPluginPaths sb should contain path to plugin`()  {
        val sb = StringBuilder()

        val plugin = Plugin().apply {
            dependencies = mutableListOf(Dependency().apply {
                groupId = "x.y"
                artifactId = "y"
                version = "1"
            })
        }

        sb.buildPluginPaths(arrayListOf("x.y:y"), plugin, "~")
        assertEquals("~/x/y/y/1/y-1.jar;", sb.toString())
    }

    @Test
    fun `a Dependency path should equal path to plugin`()  {
        val dependency = Dependency().apply {
            groupId = "x.y"
            artifactId = "z"
            version = "1"
        }

        val path = dependency asPath "~"
        assertEquals("~/x/y/z/1/z-1.jar", path)
    }

    @Test
    fun `a Dependency identifier should equal groupId artifactId`()  {
        val dependency = Dependency().apply {
            groupId = "x.y"
            artifactId = "z"
            version = "1"
        }

        val identifier = dependency.getIdentifier()
        assertEquals("x.y:z", identifier)
    }

    @Test
    fun `a String asPath dot should be replaced by slash in path`()  {
        val groupId = "x.y.z"
        val path = groupId.asPath()
        assertEquals("x/y/z", path)
    }

    @Test
    fun `an ArrayList of String buildPluginPaths StringBuilder buildPluginPaths should be called`()  {
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
        val pluginPath = stringList.buildPluginPaths(mavenProject, "~/.m2", SystemStreamLog())
        assertEquals("~/.m2/a/b/b/1/b-1.jar", pluginPath)
    }

    @Test
    fun `an ArrayList of T log list should not be modified`()  {
        val log: Log = mock {}
        val stringList = arrayListOf("x.y:y", "a.b:b", "c.d:d")
        val listAfterLog = stringList.log(log)
        assertEquals(stringList, listAfterLog)
    }
}
