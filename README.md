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
cozy runtime use latest
cozy runtime use latest-snapshot
cozy runtime install 0.2.20-SNAPSHOT
cozy runtime cache status
```

Version selectors:

- `latest` / `latest-stable`: newest non-SNAPSHOT version from Maven metadata
- `latest-snapshot`: newest SNAPSHOT version from Maven metadata
- `newest` / `recommended`: newest version from Maven metadata

Use `--runtime <version>` to override the selected runtime for one invocation:

```bash
cozy --runtime 0.2.20-SNAPSHOT sbt-bridge v1 --request /tmp/request.json
```

Use a local Cozy checkout while developing Cozy itself:

```bash
cozy --runtime-dev-dir /path/to/cozy sbt-bridge v1 --request /tmp/request.json
```

`--runtime-dev-dir` bypasses Coursier runtime resolution and runs
`sbt runMain cozy.Cozy ...` in the selected checkout.

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
  version: latest-snapshot
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
