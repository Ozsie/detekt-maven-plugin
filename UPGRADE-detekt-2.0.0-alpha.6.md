# Upgrade plan: detekt-maven-plugin -> detekt 2.0.0-alpha.6

This document is a complete, self-contained upgrade plan. It can be executed by a developer or by a coding agent. Facts stated as "verified" were checked against the published
`dev.detekt:detekt-cli:2.0.0-alpha.6` and `dev.detekt:detekt-tooling:2.0.0-alpha.6`
artifacts on Maven Central (bytecode inspection and upstream sources at tag
`v2.0.0-alpha.6`), not inferred from the changelog alone.

Reference material:

- Release notes: https://github.com/detekt/detekt/releases/tag/v2.0.0-alpha.6
- Full 2.0.0 changelog / migration guide: https://detekt.dev/changelog-2.0.0
- Upstream sources used for verification: https://github.com/detekt/detekt/tree/v2.0.0-alpha.6

## 0. Summary of the break

detekt 2.0.0 is a major break for this plugin in three ways:

1. Maven coordinates and Kotlin packages moved (`io.gitlab.arturbosch.detekt` /
   `io.github.detekt.tooling` -> `dev.detekt`).
2. The tooling API the mojos compile against changed shape:
   `MaxIssuesReached` and `--max-issues` are gone (severity-based failure via
   `--fail-on-severity`), `CliArgs.classpath` is now `List<Path>`,
   `ConfigExporter` no longer defaults its output path.
3. CLI surface changed: report ids renamed (`xml` -> `checkstyle`, `md` ->
   `markdown`), multi-value flags now use `:`/`;` splitters instead of commas,
   generated sources are excluded from analysis by default.

The plugin's architecture (in-process invocation of the detekt CLI via
`parseArguments` + `Runner(cliArgs, out, err)`) survives: both entry points
still exist at the new coordinates. `Runner(cliArgs, ...)` is annotated
`@NotApiButProbablyUsedByUsers` upstream, i.e. kept deliberately.

## 1. pom.xml changes

All in `pom.xml`.

- [ ] `detekt.version` property: `1.23.8` -> `2.0.0-alpha.6`.
- [ ] `detekt-cli` dependency: groupId `io.gitlab.arturbosch.detekt` -> `dev.detekt`.
      Keep the `jcommander` exclusion.
- [ ] `detekt-tooling` dependency: groupId `io.gitlab.arturbosch.detekt` -> `dev.detekt`.
- [ ] `jcommander.version`: `1.82` -> `1.85` (verified: detekt-cli 2.0.0-alpha.6
      declares jcommander 1.85 as a runtime dependency).
- [ ] `kotlin.version`: `2.4.0` -> `2.4.10` (detekt alpha.6 is built against Kotlin
      2.4.10). No change to `java.version` needed: all detekt 2.0 modules target
      JVM 8+ (only the ktlint wrapper targets 17).
- [ ] Decide and set the plugin's own `<version>`: convention mirrors the detekt
      version, so `2.0.0-alpha.6` is the natural choice (maintainer decision).
- [ ] Self-dogfooding config (plugin applied to itself): update
      `detekt-maven-plugin.version` to the published plugin version compatible
      with detekt 2.0 (after the new version is released; until then this can
      point at the previous release or `${project.version}` during local runs).
- [ ] Self-dogfooding report entry: `<report>xml:reports/detekt.xml</report>` ->
      `<report>checkstyle:reports/detekt.xml</report>` (report id renamed, verified
      in #8505/#8656).

## 2. Source changes (breaking API)

### 2.1 Package migration (all files under `src/main/java` and `src/test/java`)

- [ ] Replace imports:
      - `io.gitlab.arturbosch.detekt.cli.*` -> `dev.detekt.cli.*`
      - `io.github.detekt.tooling.api.*` -> `dev.detekt.tooling.api.*`

Affected files (verified): `CheckMojo.kt`, `CheckWithTypeResolutionMojo.kt`,
`CreateBaselineMojo.kt`, `GenerateConfigMojo.kt`,
`src/test/java/com/github/ozsie/CheckMojoTest.kt`,
`CheckWithTypeResolutionMojoTest.kt`.

### 2.2 `MaxIssuesReached` is gone (CheckMojo, CheckWithTypeResolutionMojo)

Verified: detekt-tooling 2.0.0-alpha.6 has no `MaxIssuesReached`. The new model
is `AnalysisResult { Detektion container; DetektError? error }` where
`DetektError : RuntimeException` with subclasses `IssuesFound`, `UnexpectedError`,
`InvalidConfig`. `Runner.execute()` throws `error.cause` when the error is an
`UnexpectedError`, otherwise throws the `DetektError` itself — so a failed
threshold surfaces as a thrown `IssuesFound`.

- [ ] In both check mojos, change the catch in `failBuildIfNeeded` from
      `MaxIssuesReached` to `dev.detekt.tooling.api.IssuesFound`. Keep the
      `failBuildOnMaxIssuesReached` semantics: rethrow unless the user asked to
      ignore findings.

### 2.3 `--max-issues` -> `--fail-on-severity` (DetektMojo)

Verified: `CliArgs.maxIssues` no longer exists. New flag `--fail-on-severity`
takes `FailureSeverity { Error (default), Warning, Info, Never }`; `Never` means
detekt never fails the run regardless of findings.

- [ ] Remove the `MAX_ISSUES` constant and its `useIf(...)` line from
      `DetektMojo.getCliSting()`.
- [ ] Add a `FAIL_ON_SEVERITY` constant (`--fail-on-severity`) and a
      `failOnSeverity` `@Parameter` (String, default empty so the CLI default
      `Error` applies).
- [ ] Keep the `maxIssues` parameter as a deprecated no-op that logs a warning
      pointing at `failOnSeverity`, or remove it (maintainer decision; see
      section 6).
- [ ] Compatibility mapping: `failBuildOnMaxIssuesReached = false` should result
      in `--fail-on-severity Never` being passed (when the user did not set
      `failOnSeverity` explicitly). This preserves the documented behavior of
      the old parameter. Consider deprecating `failBuildOnMaxIssuesReached` in
      favor of `failOnSeverity`.

### 2.4 `CliArgs.classpath` is now `List<Path>` (CheckWithTypeResolutionMojo)

Verified: `classpath: List<Path>` (`-cp`, path splitter), and it is validated by
JCommander (alpha.3, #9202) — nonexistent entries now fail fast.

- [ ] `setDefaultClasspathIfNotSet` currently assigns
      `cliArgs.classpath = elements.joinToString(File.pathSeparator)`. Change it
      to assign a `List<Path>`:
      `cliArgs.classpath = mavenProject?.compileClasspathElements?.map { Paths.get(it) }`.

### 2.5 `ConfigExporter` no longer defaults its output path (GenerateConfigMojo)

Verified upstream source at the tag:

```kotlin
override fun execute() {
    val configPath = arguments.generateConfig ?: error("Unexpected error generating config file")
    ...
}
```

`generateConfig: Path?` is set by `--generate-config` / `-gc`. The mojo never
passed `-gc`; in 1.x `ConfigExporter` fell back to a default path.

- [ ] Add a `GENERATE_CONFIG` constant (`--generate-config`) and a
      `generateConfig` `@Parameter` (String, default `config/detekt.yml`).
- [ ] In `GenerateConfigMojo`, append `-gc <resolved path>` to the CLI args
      before calling `ConfigExporter`.
- [ ] Note: the target must be a directory or not exist (alpha.3, #9131 —
      overwriting behavior was tightened). Resolve the path against
      `${basedir}`.

### 2.6 Report id renames (DetektMojo / README / pom self-check)

Verified: `xml` -> `checkstyle` (#8505, #8656), `md` -> `markdown` (#8735;
`MarkdownOutputReport.id == "markdown"`). `html` and `sarif` are unchanged.

- [ ] Update the pom self-check (done in section 1) and any README examples.
- [ ] Optional convenience for users: translate legacy `xml:` / `md:` prefixes
      in the `report` parameter to `checkstyle:` / `markdown:` and log a
      deprecation warning.
- [ ] Verify at runtime whether `md` is still accepted as an alias (the alpha.6
      CLI help text still lists `md`, but the report's registered id is
      `markdown`; both `detekt-report-md` and `detekt-report-markdown` artifacts
      exist on Central). Do not rely on the alias.
- [ ] Do not invest in the complexity/statistics reports: they ship in core for
      alpha.6 but move to first-party `dev.detekt` report plugins in alpha.7
      (upstream UNRELEASED notes).

### 2.7 New CLI options worth exposing (optional, low cost)

Verified in `CliArgs` at alpha.6:

- `--analysis-mode` (`light` | `full`; default `light`)
- `--api-version` (validated; unsupported values rejected, alpha.5 #9410)
- `--free-compiler-args`
- `--run-rule`

- [ ] Add `@Parameter`s + constants for these, or note them as not exposed in
      this release. `--analysis-mode` is the most valuable: `full` is required
      by some rules in 2.0.

Flags that survive unchanged and need no work: `-i`, `-c`, `-cr`, `-p`,
`--parallel`, `-b`, `-cb`, `-r`, `-bp`, `-dd`, `--build-upon-default-config`,
`--all-rules`, `-ac`, `--debug`, `-h`, `--jdk-home`, `--jvm-target`,
`--language-version`.

## 3. Semantics to verify at runtime (not just compile)

- [ ] **Input separator.** `-i` (and `-c`, `-p`, `-cp`, `-in`, `-ex`) now use
      JCommander splitters — the CLI help documents `:` on *nix / `;` on
      Windows, not commas. `CheckMojo.execute` and
      `CheckWithTypeResolutionMojo.execute` validate `input` by splitting on
      `,`. Align the validation with what the CLI actually parses, or
      comma-separated multi-module `input` values will validate incorrectly.
      Confirm the splitter behavior by running `mvn detekt:check` with a
      two-path input on the local machine.
- [ ] **Baseline.** `-cb` / `-b` exist and the XML format appears unchanged, but
      rules were renamed in 2.0 (`UnusedPrivateMember` -> `UnusedPrivateFunction`,
      `CommentOverPrivateFunction/Property` -> `DocumentationOverPrivate...`,
      rule set ids `documentation` -> `comments`, `empty` -> `emptyblocks`,
      `bugs` -> `potentialbugs`). Regenerate the repo's own `baseline.xml` with
      `mvn detekt:create-baseline`; stale entries suppress nothing.
- [ ] **Generated sources** are excluded from analysis by default since alpha.4
      (#9251) — behavior change for users, document it.

## 4. Tests

- [ ] `CheckMojoTest.kt`, `CheckWithTypeResolutionMojoTest.kt`: imports
      `io.github.detekt.tooling.api.MaxIssuesReached` -> `dev.detekt.tooling.api.IssuesFound`.
- [ ] Assertions on `"Build failed with 1 weighted issues."` / `"... 2 weighted ..."`
      must be re-derived: 2.0 is severity-based, the "weighted issues" wording is
      gone. Run the tests, capture the actual `IssuesFound` message, and update.
- [ ] Add a test: `failBuildOnMaxIssuesReached = false` produces
      `--fail-on-severity Never` in the constructed CLI argument list.
- [ ] Add a test: `generate-config` passes `-gc` with the resolved path.
- [ ] `GenerateConfigMojoTest.kt`: adjust for the new `-gc` requirement.

## 5. README updates

- [ ] Version numbers in all snippets.
- [ ] Report examples: `xml:` -> `checkstyle:`, `md:` -> `markdown:`.
- [ ] Document `failOnSeverity` and the deprecation/mapping of
      `failBuildOnMaxIssuesReached` and `maxIssues`.
- [ ] Short "upgrading to detekt 2" section for plugin users covering:
      - config rule set renames (section 3),
      - rule renames listed in the 2.0 changelog,
      - generated sources excluded by default,
      - YAML parser becoming strict about types (`active: "true"` will stop
        parsing; upstream UNRELEASED after alpha.6 — mention as upcoming),
      - plugin jars: `io.gitlab.arturbosch.detekt:detekt-formatting` ->
        `dev.detekt:detekt-rules-ktlint-wrapper`.

## 6. Open decisions (resolve before implementing)

1. Plugin version: `2.0.0-alpha.6` mirroring detekt (recommended, matches
   project convention) or an independent scheme.
2. `maxIssues` parameter: remove (breaking) or keep as deprecated no-op with a
   warning pointing at `failOnSeverity`.
3. `failBuildOnMaxIssuesReached`: keep as sugar over `--fail-on-severity Never`
   (recommended for backward compatibility) or replace it.

## 7. Verification checklist

- [ ] `mvn clean verify` passes (compiles and dogfoods the plugin on itself).
- [ ] Smoke: `mvn detekt:check` produces `reports/detekt.xml` in checkstyle
      format; findings fail the build with `IssuesFound`.
- [ ] Smoke: `mvn detekt:check -Ddetekt.failBuildOnMaxIssuesReached=false` does
      not fail the build.
- [ ] Smoke: `mvn detekt:generate-config` writes the default config to the
      expected path.
- [ ] Smoke: `mvn detekt:create-baseline` then `mvn detekt:check` consumes it.
- [ ] Multi-path `input` works and validation logic agrees with CLI parsing.
- [ ] Update any committed report expectations (e.g. `reports/detekt.xml`) if
      they are meant to track the dogfood run.

## 8. Implementation order suggestion

1. pom.xml (section 1) — gets new artifacts onto the classpath.
2. Package renames (2.1) — mechanical; compile to surface every break.
3. 2.2-2.5 — required for compilation.
4. Tests (4) — make the suite honest again.
5. Runtime verification (3, 7) — smoke goals, regenerate baseline.
6. Optional items (2.6 alias translation, 2.7 new flags) and README (5).
