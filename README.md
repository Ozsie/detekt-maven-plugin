#Detekt Maven Plugin
A maven plugin that wraps the Detekt CLI. It supports the same parameters as the Detekt CLI.

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
                    <configuration>
                        <config>${basedir}/detekt.yml</config>
                        <plugins>
                            <plugin>com.github.ozsie:detekt-coverity-report</plugin>
                        </plugins>
                    </configuration>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>com.github.ozsie</groupId>
                    <artifactId>detekt-coverity-report</artifactId>
                    <version>${detekt.coverity.version}</version>
                </dependency>
            </dependencies>
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