package com.github.ozsie

import org.apache.maven.model.Dependency

internal infix fun Dependency.asPath(root: String) =
        "$root/${groupId.asPath()}/$artifactId/$version/$artifactId-$version.jar"

internal fun Dependency.getIdentifier() = "$groupId:$artifactId"