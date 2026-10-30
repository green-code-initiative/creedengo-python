Please read common [CONTRIBUTING.md](https://github.com/green-code-initiative/creedengo-common/blob/main/doc/CONTRIBUTING.md) in `creedengo-common` repository.

## Maven and integration tests

Use `./mvnw` to build this repository. Integration tests also launch `mvn`
directly, so put the wrapper distribution first in `PATH` when running them locally:

```sh
MVN_HOME="$(./mvnw -B -v | sed -n 's/^Maven home: //p')"
test -x "$MVN_HOME/bin/mvn" || exit 1
export PATH="$MVN_HOME/bin:$PATH"
mvn -v
./mvnw -B verify
```

The `sonar:sonar` prefix used by integration tests must also resolve: configure
`org.sonarsource.scanner.maven` in Maven settings `pluginGroups`, as shown in
the build workflows.

See [TECH_DEBT.md](TECH_DEBT.md) for the Maven 3.10 compatibility workaround,
integration-test classpath isolation, cleanup criteria, and dependency-submission
settings required after merging the explicit workflow.
