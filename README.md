[![Build Status](https://github.com/Ozsie/detekt-maven-plugin/actions/workflows/maven.yml/badge.svg)](https://github.com/Ozsie/detekt-maven-plugin/actions/workflows/maven.yml)
[![Deploy](https://github.com/Ozsie/detekt-maven-plugin/actions/workflows/deploy.yml/badge.svg)](https://github.com/Ozsie/detekt-maven-plugin/actions/workflows/deploy.yml)
[![codecov](https://codecov.io/gh/Ozsie/detekt-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/Ozsie/detekt-maven-plugin)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FOzsie%2Fdetekt-maven-plugin.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2FOzsie%2Fdetekt-maven-plugin?ref=badge_shield)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.ozsie/detekt-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ozsie%22%20AND%20a:%22detekt-maven-plugin%22)

# Detekt Maven Plugin

A [Maven](https://maven.apache.org) [plugin](https://maven.apache.org/plugins/index.html) that wraps the [Detekt](https://detekt.dev/) CLI. It supports the same parameters as the [Detekt CLI](https://detekt.dev/docs/gettingstarted/cli).

## How to use
See [below](#goals) how to execute after configuring.

### Basic usage
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.23.7</version>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
Using the above configuration Detekt will scan source files in `${basedir}/src` and output the results in `${basedir}/detekt`.  

## Configuration
All parameters available to Detekt version _1.23.7_ can be configured in the plugin.

### Local rule configuration
The plugin supports local files as configuration to be passed to Detekt.
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.23.7</version>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                    <configuration>
                        <config>config/detekt/detekt.yml</config>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
Multiple files can be listed with `;` as separator.

### Remote rule configuration
The plugin supports remote config over http and https.
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.23.7</version>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                    <configuration>
                        <config>https://raw.githubusercontent.com/Ozsie/detekt-maven-plugin/fd0de6d59e6ae1e062a9d2b030a171da1d3225ab/src/test/resources/resolve-config/remote/remote-config.yml</config>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Extensions
Detekt supports [rulesets, processors, reports, etc.](https://detekt.dev/docs/introduction/extensions) packaged in Detekt plugins.

#### Published Detekt plugins
See [Detekt Marketplace](https://detekt.dev/marketplace) for list of known plugins.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.23.7</version>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                </execution>
            </executions>
            <dependencies>
                <!-- Detekt's first-party unbundled plugin for library authors:
                     https://detekt.dev/docs/next/rules/libraries -->
                <dependency>
                    <groupId>io.gitlab.arturbosch.detekt</groupId>
                    <artifactId>detekt-rules-libraries</artifactId>
                    <version>1.23.7</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

#### Local Detekt plugins
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.23.7</version>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                    <configuration>
                        <plugins>
                            <plugin>local/path/to/plugin.jar</plugin>
                        </plugins>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Reporting
See [Detekt documentation](https://detekt.dev/docs/introduction/reporting) for supported report types.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.23.7</version>
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

Alternatively, the configuration can be placed outside of the `<executions>`.
This allows the configuration be used when running goals standalone.
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.23.7</version>
            <configuration>
                <report>
                    <report>txt:reports/detekt.txt</report>
                    <report>xml:reports/detekt.xml</report>
                </report>
            </configuration>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Baseline
First a [baseline file](https://detekt.dev/docs/introduction/baseline) needs to be generated:
```bash
mvn detekt:cb
```
This will generate a baseline file for each module named as `baseline-<module-name>.xml`. For more information on generating baselines, see [create-baseline](#create-baseline). You can now reference the baseline file in your configuration, as below.
```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.23.7</version>
            <configuration>
                <baseline>baseline.xml</baseline>
            </configuration>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>check</goal></goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Type Resolution

See [Issue #144](https://github.com/Ozsie/detekt-maven-plugin/issues/144) for an explanation.

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>3.2.0</version>
            <executions>
                <execution>
                    <id>generate-classpath-var</id>
                    <phase>package</phase>
                    <goals><goal>build-classpath</goal></goals>
                    <configuration>
                        <outputProperty>generated.classpath</outputProperty>
                        <silent>true</silent>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>com.github.ozsie</groupId>
            <artifactId>detekt-maven-plugin</artifactId>
            <version>1.23.7</version>
            <configuration>
                <baseline>baseline.xml</baseline>
                <classPath>${generated.classpath}</classPath>
                <jvmTarget>17</jvmTarget>
            </configuration>
            <executions>
                <execution>
                    <phase>verify</phase>
                    <goals><goal>ctr</goal></goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Goals
### `check`

Used to run detekt. All cli parameters, excluding -gc and -cb, are available using -Ddetekt.{parameter}

_Examples_

 * `mvn detekt:check -Ddetekt.config=detekt.yml`
 * `mvn detekt:check -Ddetekt.debug=true`

If you need type resolution, use the following instead

 * `mvn detekt:ctr -Ddetekt.config=detekt.yml`
 * `mvn detekt:ctr -Ddetekt.debug=true`

### `create-baseline`

Used to create a baseline. All cli parameters, excluding -gc and -cb,
are available using -Ddetekt.{parameter}. By default, a file called
baseline-<module-name>.xml will be generated. If you include -Ddetekt.baseline in the
call you can specify some other name for the baseline file.

_Examples_

*  `mvn detekt:cb -Ddetekt.config=detekt.yml`
 * `mvn detekt:cb -Ddetekt.debug=true`
*  `mvn detekt:cb -Ddetekt.baseline=some-other-baseline.xml`
*  `mvn detekt:create-baseline -Ddetekt.config=detekt.yml`
 * `mvn detekt:create-baseline -Ddetekt.debug=true`

### `generate-config`

Used to generate a default configuration file.

_Example_

 * `mvn detekt:gc`
 * `mvn detekt:generate-config`

## Contributors
<a href="https://github.com/ozsie/detekt-maven-plugin/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=ozsie/detekt-maven-plugin" alt="contributors"/>
</a>

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FOzsie%2Fdetekt-maven-plugin.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FOzsie%2Fdetekt-maven-plugin?ref=badge_large)
