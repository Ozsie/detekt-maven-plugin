package com.github.ozsie.test

import com.github.ozsie.CTRMojo
import com.github.ozsie.CheckWithTypeResolutionMojoTest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.apache.maven.model.Dependency
import org.apache.maven.model.Plugin
import org.apache.maven.project.MavenProject
import java.nio.file.Paths

object CheckWithTypeResolutionMojoTestFactory {

    private val invalidPackageNamingDirectoryPath by lazy {
        val uri = CheckWithTypeResolutionMojoTest::class.java.classLoader
            .getResource("code-samples/invalid-package-naming")?.toURI() ?: error("Failed to load resource")
        Paths.get(uri).toString()
    }

    private val validPackageNamingDirectoryPath by lazy {
        val uri = CheckWithTypeResolutionMojoTest::class.java.classLoader
            .getResource("code-samples/valid")?.toURI() ?: error("Failed to load resource")
        Paths.get(uri).toString()
    }

    private fun create(block: CTRMojo.() -> Unit = {}): CTRMojo {
        return CTRMojo().apply {
            input = validPackageNamingDirectoryPath
            mavenProject = createMockMavenProject()
            block(this)
        }
    }

    fun createWithInvalidPackageNamingStructure(block: CTRMojo.() -> Unit): CTRMojo {
        return CTRMojo().apply {
            input = invalidPackageNamingDirectoryPath
            block(this)
        }
    }

    fun createWithNoRuleExecution(block: CTRMojo.() -> Unit = {}): CTRMojo {
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
