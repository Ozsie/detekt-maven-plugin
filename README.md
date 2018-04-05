# Detekt Maven Plugin
A maven plugin that wraps the Detekt CLI. It supports the same parameters as the Detekt CLI.

## How to use
### Basic configuration
```xml
<build>
    <plugin>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.0.0.RC6-4</version>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                </execution>
            </executions>
        </plugin>
    </plugin>
</build>

<pluginRepositories>
    <pluginRepository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-ozsie-maven</id>
        <name>bintray-plugins</name>
        <url>https://dl.bintray.com/ozsie/maven</url>
    </pluginRepository>
</pluginRepositories>
```
Using the above configuration, Detekt will scan source files in _${basedir}/src_ and output the results in _${basedir}/detekt_.

All parameters available to Detekt version _1.0.0.RC6_3_ can be configured in the plugin.

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
 
