package com.github.ozsie

import org.junit.jupiter.api.Test
import kotlin.test.expect

class CreateBaselineMojoTest {
    @Test
    fun `a CreateBaselineMojo and 'skip' is true Unit is expected`()  {
        val createBaselineMojo = CreateBaselineMojo()
        createBaselineMojo.skip = true
        expect(Unit) {
            createBaselineMojo.execute()
        }
    }

    @Test
    fun `a CreateBaselineMojo and 'skip' is false Unit is expected`()  {
        val createBaselineMojo = CreateBaselineMojo()
        createBaselineMojo.skip = false
        expect(Unit) {
            createBaselineMojo.execute()
        }
    }

    @Test
    fun `a CBMojo and 'skip' is true Unit is expected`()  {
        val createBaselineMojo = CBMojo()
        createBaselineMojo.skip = true
        expect(Unit) {
            createBaselineMojo.execute()
        }
    }

    @Test
    fun `a CBMojo and 'skip' is false Unit is expected`()  {
        val createBaselineMojo = CBMojo()
        createBaselineMojo.skip = false
        expect(Unit) {
            createBaselineMojo.execute()
        }
    }
}
