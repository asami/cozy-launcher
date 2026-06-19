# cozy-launcher

Launcher for the Cozy CLI.

The `cozy` Coursier app entry points at this launcher. The launcher resolves a
selected Cozy runtime artifact, then invokes `cozy.Cozy` in the same JVM.
Normal Cozy CLI arguments are passed through:

```bash
cozy modeler-scala src/main/cozy/model.cml --save target/cozy
cozy sbt-bridge v1 --request /tmp/request.json
```

## Runtime

```bash
cozy runtime current
cozy runtime use recommended
cozy runtime use latest-snapshot
cozy runtime install 0.2.20-SNAPSHOT
cozy runtime cache status
cozy runtime catalog show
```

Version selectors:

- `recommended`: operator-selected default runtime from the catalog.
- `latest` / `latest-stable`: newest stable runtime in the catalog.
- `latest-snapshot`: newest snapshot runtime in the catalog.
- `newest`: newest enabled runtime across all catalog channels.

Use `--runtime <version>` to override the selected runtime for one invocation:

```bash
cozy --runtime 0.2.20-SNAPSHOT sbt-bridge v1 --request /tmp/request.json
```

Use a local Cozy checkout while developing Cozy itself:

```bash
cozy --runtime-dev-dir /path/to/cozy sbt-bridge v1 --request /tmp/request.json
```

`--runtime-dev-dir` bypasses Coursier runtime resolution, resolves the selected
checkout's `Runtime / fullClasspath`, and invokes `cozy.Cozy` through java direct
execution. sbt is used only to export the classpath when the cached
`target/cozy.d/runtime-classpath.txt` is missing.

## Configuration

Launcher configuration is read from:

```text
~/.cozy/launcher.yaml
$PWD/conf/cozy/launcher.yaml
$PWD/.cozy/launcher.yaml
```

Example:

```yaml
runtime:
  version: recommended
  catalog:
    url: https://www.simplemodeling.org/repository/cozy/runtime-catalog.yaml
  dev-dir: /path/to/cozy

repositories:
  coursier:
    - ivy2Local
    - central
  maven:
    - https://www.simplemodeling.org/repository/maven
```

`.cozy/config.yaml` remains Cozy build/publish configuration and is not read as
launcher configuration.

## Coursier Channel

Install the app from the channel:

```bash
cs install cozy --channel https://www.simplemodeling.org/repository/cozy/coursier-channel.json
```

Publication is intentionally constrained by version type:

```bash
# Public release versions are published to the repository.
sbt publish

# Development SNAPSHOT versions are local-only.
sbt publishLocal
```

`publishLocal` is refused for release versions. `publish` is refused for
SNAPSHOT versions.

`publish` also updates:

```text
repository/cozy/coursier-channel.json
```

with the `cozy` app entry:

```json
{
  "cozy": {
    "repositories": [
      "central",
      "https://www.simplemodeling.org/repository/maven"
    ],
    "dependencies": [
      "org.simplemodeling:cozy-launcher_3:<version>"
    ],
    "mainClass": "cozy.launcher.CozyLauncherMain"
  }
}
```
