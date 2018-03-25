#Detekt Maven Plugin
A maven plugin that wraps the Detekt CLI. It supports the same parameters as the Detekt CLI.

```xml
<build>
    <plugin>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <configuration>
                <input>${basedir}/src</input>
                <filters>.*test.*</filters>
                <output>${basedir}/detekt</output>
                <config>${basedir}/detekt.yml</config>
                <plugins>
                    <plugin>com.github.ozsie:detekt-coverity-report</plugin>
                </plugins>
            </configuration>
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