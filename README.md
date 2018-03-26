# Detekt Maven Plugin
A maven plugin that wraps the Detekt CLI. It supports the same parameters as the Detekt CLI.

## How to use
**Basic configuration**
```xml
<build>
    <plugin>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <id>detekt</id>
                    <phase>verify</phase>
                    <goals><goal>detekt</goal></goals>
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

All parameters available to Detekt version _1.0.0.RC6_3_ can be configured in the plugin