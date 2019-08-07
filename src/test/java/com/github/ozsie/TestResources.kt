package com.github.ozsie

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.apache.maven.project.MavenProject
import java.io.File
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Given a relative path, resolves it into a File within the project's test resources
 *
 * @return a File handle to the resource
 * @throws AssertionError if resource does not exist
 */
internal fun resolveTestResourcePath(path: String): File {
    val url = ResolveConfigSpec::class.java.classLoader.getResource(path)
    assertNotNull(url, "Cannot find test resource path $path")
    return File(url.toURI())
}

/**
 * Given a relative resource path, returns a mock Maven Project with its basedir set
 * to the resolved path.
 * @throws AssertionError if resolved base directory doesn't exist
 */
internal fun projectWithBasedirAt(resourcePath: String): MavenProject {
    val resolved = resolveTestResourcePath(resourcePath)
    val base = if (resolved.isFile) {
        resolved.parentFile
    } else {
        resolved
    }
    assertTrue(base.exists(), "Expected directory at $base to exist")

    return mock {
        on {
            basedir
        } doReturn base
    }
}
