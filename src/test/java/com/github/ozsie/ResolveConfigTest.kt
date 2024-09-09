package com.github.ozsie

import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

const val REMOTE_HOST = "https://raw.githubusercontent.com"
const val REMOTE_REPO = "$REMOTE_HOST/Ozsie/detekt-maven-plugin/fd0de6d59e6ae1e062a9d2b030a171da1d3225ab"
const val REMOTE_CONFIG_URL = "$REMOTE_REPO/src/test/resources/resolve-config/remote/remote-config.yml"
// Note: while annoying, the use of File() in the asserts should maximize cross-platform compatibility

class ResolveConfigTestProjectTest {
    private val project = projectWithBasedirAt("resolve-config")
    private val basedir = project.basedir

    @Test
    fun `a relative config name resolveConfig resolves the file name to an absolute path`()  {
        val config = "one.yml"
        val result = resolveConfig(project, config)
        assertEquals(
            File(basedir, "one.yml").absolutePath,
            result
        )
    }

    @Test
    fun `an absolute config name returns the input`()  {
        val config = resolveTestResourcePath("resolve-config/nested/three.yml").absolutePath
        val result = resolveConfig(project, config)
        assertEquals(config, result)
    }

    @Test
    fun `semicolon-separated config names resolves all config files`()  {
        val config = "one.yml;two.yml"
        val result = resolveConfig(project, config)
        assertEquals(
            "${File(basedir, "one.yml")};${File(basedir, "two.yml")}",
            result
        )
    }

    @Test
    fun `comma-separated config names resolves all config files`()  {
        val config = "one.yml,two.yml"
        val result = resolveConfig(project, config)
        assertEquals(
            "${File(basedir, "one.yml")};${File(basedir, "two.yml")}",
            result
        )
    }

    @Test
    fun `a non-existent path FileNotFound exception is thrown`()  {
        val config = "fake.yml"
        assertFailsWith<FileNotFoundException> { resolveConfig(project, config) }
    }
}

class ResolveConfigTestNestedProjectTest {
    private val project = projectWithBasedirAt("resolve-config/nested")
    private val basedir = project.basedir
    private val parentDir = basedir.parentFile

    @Test
    fun `a file in the parent project resolves to the parent directory`()  {
        val config = "one.yml"
        val result = resolveConfig(project, config)
        assertEquals(
            File(parentDir, "one.yml").absolutePath,
            result
        )
    }

    @Test
    fun `multiple config names`()  {
        val config = "one.yml;three.yml"
        val result = resolveConfig(project, config)
        assertEquals(
            "${File(parentDir, "one.yml")};${File(basedir, "three.yml")}",
            result
        )
    }
}

class ResolveConfigRemoteConfigFileTest {
    private val project = projectWithBasedirAt("resolve-config")

    @Test
    fun `remote file exists resolves the remote file`()  {
        val result = resolveConfig(project, REMOTE_CONFIG_URL)
        val expected = project.basedir.absolutePath + EXPORTED_FILE_LOCATION
        assertEquals(expected, result)
        assertTrue(Files.exists(Paths.get(project.basedir.absolutePath + EXPORTED_FILE_LOCATION)))
    }

    @Test
    fun `remote file does not exist resolves the remote file`()  {
        val config = "$REMOTE_REPO/does-not-exist.yml"
        val exception = assertFailsWith<FileNotFoundException> {
            resolveConfig(project, config)
        }
        assertEquals(config, exception.message)
    }
}

