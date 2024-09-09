package com.github.ozsie

import java.io.File
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import kotlin.test.expect

class GenerateConfigMojoTest {
    @Test
    fun `a GenerateConfigMojo and 'skip' is true`()  {
        val generateConfigMojo = GenerateConfigMojo()
        generateConfigMojo.skip = true
        expect(Unit) {
            generateConfigMojo.execute()
        }
    }

    @Test
    fun `a GenerateConfigMojo and 'skip' is false`()  {
        val generateConfigMojo = GenerateConfigMojo()
        generateConfigMojo.skip = false
        generateConfigMojo.execute()
        val file = File("detekt.yml")
        assertTrue(file.exists())
        file.deleteOnExit()
    }

    @Test
    fun `a GCMojo and 'skip' is true`()  {
        val generateConfigMojo = GCMojo()
        generateConfigMojo.skip = true
        expect(Unit) {
            generateConfigMojo.execute()
        }
    }

    @Test
    fun `a GCMojo and 'skip' is false`()  {
        val generateConfigMojo = GCMojo()
        generateConfigMojo.skip = false
        generateConfigMojo.execute()
        val file = File("detekt.yml")
        assertTrue(file.exists())
        file.deleteOnExit()
    }
}
