[![Build Status](https://travis-ci.org/Ozsie/detekt-maven-plugin.svg?branch=master)](https://travis-ci.org/Ozsie/detekt-maven-plugin)
[![codecov](https://codecov.io/gh/Ozsie/detekt-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/Ozsie/detekt-maven-plugin)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FOzsie%2Fdetekt-maven-plugin.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FOzsie%2Fdetekt-maven-plugin?ref=badge_shield)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.ozsie/detekt-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ozsie%22%20AND%20a:%22detekt-maven-plugin%22)

# Detekt Maven Plugin

A maven plugin that wraps the Detekt CLI. It supports the same parameters as the Detekt CLI.

## How to use
### Basic configuration
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```ThatJoeMo
Using the above configuration, Detekt will scan source files in _${basedir}/src_ and output the results in _${basedir}/detekt_.

All parameters available to Detekt version _1.0.0_ can be configured in the plugin.

##Specify report files
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                    <configuration>
                        <report>
                            <report>txt:reports/detekt.txt</report>
                            <report>xml:reports/detekt.xml</report>
                        </report>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
### Goals
***check***

Used to run detekt. All cli parameters, excluding -gc and -cb, are available using -Ddetekt.{parameter}

_Examples_

 * `mvn detekt:check -Ddetekt.config=detekt.yml`
 * `mvn detekt:check -Ddetekt.debug=true`

***create-baseline***

Used to create a baseline. All cli parameters, excluding -gc and -cb, are available using -Ddetekt.{parameter}

_Examples_

 * `mvn detekt:cb -Ddetekt.config=detekt.yml`
 * `mvn detekt:cb -Ddetekt.debug=true`
 * `mvn detekt:create-baseline -Ddetekt.config=detekt.yml`
 * `mvn detekt:create-baseline -Ddetekt.debug=true`

***generate-config***

Used to generate a default configuration file

_Example_

 * `mvn detekt:gc`
 * `mvn detekt:generate-config`
 
 For more information on Detekt, have a look at https://github.com/arturbosch/detekt
 
## Contributors
 * [andreysaksonov](https://github.com/andreysaksonov)
 * [josephlbarnett](https://github.com/josephlbarnett)
 * [reubenfirmin](https://github.com/reubenfirmin)
 * [ThatJoeMoore](https://github.com/ThatJoeMoore)
 * [schalkms](https://github.com/schalkms)

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FOzsie%2Fdetekt-maven-plugin.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FOzsie%2Fdetekt-maven-plugin?ref=badge_large)
