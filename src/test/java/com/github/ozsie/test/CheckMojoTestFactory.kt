package com.github.ozsie.test

import com.github.ozsie.CheckMojo
import com.github.ozsie.CheckMojoTest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.apache.maven.model.Dependency
import org.apache.maven.model.Plugin
import org.apache.maven.project.MavenProject
import java.nio.file.Paths

object CheckMojoTestFactory {

    private val invalidPackageNamingDirectoryPath by lazy {
        val uri = CheckMojoTest::class.java.classLoader
            .getResource("code-samples/invalid-package-naming")!!.toURI()
        Paths.get(uri).toString()
    }

    private val validPackageNamingDirectoryPath by lazy {
        val uri = CheckMojoTest::class.java.classLoader
            .getResource("code-samples/valid")!!.toURI()
        Paths.get(uri).toString()
    }

    fun create(block: CheckMojo.() -> Unit = {}): CheckMojo {
        return CheckMojo().apply {
            input = validPackageNamingDirectoryPath
            mavenProject = createMockMavenProject()
            block(this)
        }
    }

    fun createWithInvalidPackageNamingStructure(block: CheckMojo.() -> Unit): CheckMojo {
        return CheckMojo().apply {
            input = invalidPackageNamingDirectoryPath
            block(this)
        }
    }

    fun createWithNoRuleExecution(block: CheckMojo.() -> Unit = {}): CheckMojo {
        return create {
            disableDefaultRuleSets = true
            block(this)
        }
    }

    private fun createMockMavenProject(): MavenProject {
        return mock {
            on {
                compileClasspathElements
            } doReturn listOf("/tmp/default", "/tmp/default2")
            on {
                getPlugin(any())
            } doReturn (
                Plugin().apply {
                    dependencies = mutableListOf(
                        Dependency().apply {
                            groupId = "a.b"
                            artifactId = "b"
                            version = "1"
                        }
                    )
                }
                )
        }
    }
}
