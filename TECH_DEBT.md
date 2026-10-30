# Technical debt

## TD-1 - Maven 3.10 and the versioning extension

### Problem and affected surfaces

The repository loads `me.qoomon:maven-git-versioning-extension:9.12.1` from
`.mvn/extensions.xml`. This extension uses
`org.apache.maven.shared.utils.logging.MessageUtils`, provided by
`maven-shared-utils`. Maven 3.10 no longer supplies that library, so loading the
extension can fail with `NoClassDefFoundError`.

The wrapper pins Maven 3.9.9 and works with this extension. Running the main
build with `./mvnw` alone is not sufficient: integration tests launch `mvn`
directly to analyze the nested test project. Without its own `.mvn` directory,
that project discovers the repository's Maven root and inherits the extension.
The runner's Maven can also be used by release builds and GitHub's automatic
dependency-submission workflow.

### Current workarounds

| Surface | Files | Workaround |
| --- | --- | --- |
| Main builds and nested Maven processes | `.github/workflows/build.yml`, `.github/workflows/build-jdk17.yml` | Resolve `Maven home` using `./mvnw -B -v`, verify `bin/mvn` exists, append that exact `bin` to `GITHUB_PATH`, then log `mvn -v`. Do not select an arbitrary cached distribution. |
| Release packaging | `.github/workflows/tag_release.yml` | Build with `./mvnw` instead of runner `mvn`. |
| Dependency submission | `.github/workflows/dependency-submission.yml` | Run an explicit workflow with the same wrapper-derived `PATH`, JDK 21 and Maven cache. Grant `contents: write` only to its job; skip fork and Dependabot pull requests with read-only tokens. |
| Independent analysis project | `src/it/test-projects/creedengo-python-plugin-test-project/.mvn/maven.config` | Comments only: the `.mvn` directory establishes a separate Maven root without adding arguments or loading the repository extension. |
| Tracking the test configuration | `.gitignore` | Allow only that test project's `.mvn` directory through the dotfile ignore rule. |
| Local development | `CONTRIBUTING.md` | Document wrapper-derived `PATH` for integration tests and scanner prefix configuration. |

### Repository setting after integration

After merging the explicit workflow, a repository administrator must open
**Settings > Advanced Security > Dependency graph**:

- Keep **Dependency graph** enabled.
- Disable **Automatic dependency submission** to stop GitHub's generated Maven
  workflow from running with an uncontrolled Maven installation.

The explicit workflow replaces automatic submission, not the dependency graph.
It runs on pushes to `main`, eligible pull requests (`opened`, `synchronize`,
`reopened`), and manual dispatch. No remote repository setting is changed by
this patch.

### Cleanup procedure

1. Upgrade the versioning extension to a release verified compatible with Maven
   3.10. Check its upstream release notes rather than assuming a version fixes it.
2. Test the main build, release packaging, dependency submission, and nested test
   analysis with Maven 3.10, including both JDK 17 and JDK 21 build configurations.
3. Remove the temporary wrapper-derived `PATH` steps and associated comments
   once direct `mvn` invocations are proven compatible. Keeping `./mvnw` for
   reproducible main/release builds is still recommended.
4. Remove the test project's comment-only `.mvn/maven.config` and the targeted
   `.gitignore` exception if root isolation is no longer needed. Recheck nested
   analysis after removal.
5. Decide whether to retain controlled, explicit dependency submission or restore
   GitHub automatic submission. If restoring automatic submission, verify it
   succeeds before removing the explicit workflow; do not keep both active.
6. Update this document and `CONTRIBUTING.md` to reflect the verified setup.

## TD-2 - Integration-test analyzer classpath isolation

This is separate from Maven 3.10 compatibility.

`org.sonarsource.python:sonar-python-plugin` is required for compilation and unit
tests, but is installed into the SonarQube server by Failsafe's `test-it.plugins`.
It is not needed in the Failsafe JVM. Its bundled classes can mask dependencies
used by `sonar-orchestrator`.

The Failsafe execution in `pom.xml` excludes only that analyzer artifact using
`classpathDependencyExcludes`. Its installation into SonarQube and the unit-test
classpath remain unchanged. `creedengo-integration-test` remains at **0.6.0**.
In particular, when upgrading to **0.7.0 or later**, check for bundled
`commons-lang3` conflicts: `ObjectUtils.getIfNull(Object, Object)` requires
`commons-lang3` 3.18 or later. This does not claim that 0.7.0 is already installed
or that the current build has that exact failure.

Keep the exclusion while the analyzer bundles conflicting classes. Reconsider
it only after inspecting the analyzer and orchestrator dependency/classloading
behavior and successfully running both unit and integration tests without it.
Do not remove it merely because the Maven versioning extension is upgraded.

## References

- Original cross-language correction: [green-code-initiative/creedengo-php#164](https://github.com/green-code-initiative/creedengo-php/pull/164).
- [Upstream Maven Git Versioning Extension](https://github.com/qoomon/maven-git-versioning-extension).
- [Runner images Maven issue](https://github.com/actions/runner-images/issues/14883).
- [Maven 3.10.0 release notes](https://maven.apache.org/docs/3.10.0/release-notes.html).
