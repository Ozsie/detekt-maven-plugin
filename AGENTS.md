# AGENTS.md

Guidance for coding agents (and humans) working in this repository.

## What this repo is

`com.github.ozsie:detekt-maven-plugin` — a Maven plugin that wraps the detekt
CLI for Kotlin static analysis. It does **not** shell out to a `detekt`
executable: the mojos build a CLI argument list, parse it in-process with
detekt's `parseArguments`, and execute detekt via its `Runner`. Everything is
in-process against `detekt-cli` / `detekt-tooling` artifacts declared in
`pom.xml`.

The plugin's version tracks the wrapped detekt version (e.g. `1.23.8`). When
changing `detekt.version` in the pom, decide whether the plugin's own version
should follow.

## Commands

```bash
# Full build: compile + tests + plugin descriptor + dogfooded detekt check
mvn clean verify

# Tests only (JUnit 5 via kotlin-test; surefire picks up **/*Test.*)
mvn test

# Build without running anything detekt-related
mvn clean install -DskipTests -Ddetekt.skip

# Dogfood: run the plugin on itself (CI does this on JDK 8/11/17)
mvn detekt:check -Ddetekt.baseline=baseline.xml
```

- `-Ddetekt.skip=true` skips the dogfooded check (used in CI build jobs).
- The plugin is applied to itself in `pom.xml` (`check` goal, `checkstyle`/
  `xml` report to `reports/detekt.xml`, `baseline.xml`) — your changes to the
  mojos are exercised by the project's own build.
- CI runs on ubuntu + windows, JDK 8/11/17. Don't introduce code that requires
  a newer JVM: `java.version` in the pom is the compatibility floor (1.8).

## Layout

```
src/main/java/com/github/ozsie/   Kotlin sources (yes, under java/ — do not move)
src/test/java/com/github/ozsie/   Kotlin tests
src/test/resources/code-samples/  Kotlin fixtures the tests run detekt against
src/test/resources/resolve-config/  config-resolution fixtures (incl. remote)
```

## Code style

- Kotlin, 4-space indent, no formatter enforced (ktfmt is disabled in
  `.idea/ktfmt.xml`) — match the existing style of the file you touch.
- Minimal comments; the codebase is nearly comment-free. Don't add
  narration comments.
- CLI flags are `const val` strings, defined once in `DetektMojo.kt`, then used
  through `ArrayList.useIf(...)` in `DetektMojo.getCliSting()`. New flags
  follow that pattern.
- Mojo parameters are `@Parameter(property = "detekt.<name>")` fields on
  `DetektMojo` (shared) or the specific mojo. Property names are public API —
  changing them breaks users.
- One mojo per goal; short-alias mojos are one-liners
  (`@Mojo(name = "cb") class CBMojo : CreateBaselineMojo()`).
- Extension functions live in `ListExtensions.kt` / `DependencyExtensions.kt` /
  `StringExtensions.kt`, not in the mojos.
- Tests use backtick sentence names (`fun \`a CheckMojo and 'skip' is
  true Unit is expected\`()`), `kotlin.test` (`expect`, `assertFailsWith`) on
  JUnit 5. New tests follow this.
- Tests exercise the real mojos against `src/test/resources/code-samples/`
  fixtures — prefer adding a fixture over mocking.

## detekt 2.0 upgrade (in progress)

The upgrade to detekt `2.0.0-alpha.6` is planned in
[`UPGRADE-detekt-2.0.0-alpha.6.md`](./UPGRADE-detekt-2.0.0-alpha.6.md). Read it
before touching detekt-facing code. Key invariants to know even for unrelated
changes:

- detekt 2.0 lives under Maven group `dev.detekt` and Kotlin packages
  `dev.detekt.*` (1.x: `io.gitlab.arturbosch.detekt` / `io.github.detekt.tooling`).
  During any transition, check which line the pom's `detekt.version` targets.
- `detekt-cli` is a runtime wrapper the plugin embeds; its JCommander
  dependency is deliberately excluded in the pom and pinned separately —
  keep that exclusion in sync with detekt-cli's declared version.
- The CLI argument list built in `getCliSting()` is the integration surface;
  anything detekt rejects there (renamed flags, report ids `xml` ->
  `checkstyle`, `md` -> `markdown`, typed/validated options) breaks users at
  runtime, not at compile time. When changing flags, verify against the
  `CliArgs` of the exact detekt version in the pom.
- Rules get renamed across detekt majors; after a detekt bump, regenerate
  `baseline.xml` (`mvn detekt:create-baseline`) or stale entries silently
  suppress nothing.

## Release

Releases are cut by publishing a GitHub release; `deploy.yml` builds with
`mvn package` and publishes to OSSRH with GPG signing. Don't touch release/
deploy config or run `mvn deploy` unless explicitly asked.
