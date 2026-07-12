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
ancestor conf/cozy/launcher.yaml and .cozy/launcher.yaml files, outermost first
$PWD/conf/cozy/launcher.yaml
$PWD/.cozy/launcher.yaml
```

Ancestor discovery lets a workspace such as `cncf-samples` or sbt scripted
tests keep one root `.cozy/launcher.yaml` for nested sample and fixture
directories. A nested directory can still override the inherited settings with
its own `conf/cozy/launcher.yaml` or `.cozy/launcher.yaml`.

`conf/cozy/launcher.yaml` is shared launcher configuration when it is intentionally public.
`.cozy/launcher.yaml` is a local sensitive launcher/runtime override and should normally be git-ignored.
Normal Cozy/BoK operation settings belong in `conf/cozy/config.yaml`; local sensitive operation overrides belong in `.cozy/config.yaml`.

Example:

```yaml
development:
  enabled: true
  launcher:
    dev-dir: /Users/asami/src/dev2026/cozy-launcher
  runtime:
    dev-dir: /Users/asami/src/dev2025/cozy

runtime:
  version: recommended
  catalog:
    url: https://www.simplemodeling.org/repository/cozy/runtime-catalog.yaml

repositories:
  coursier:
    - ivy2Local
    - central
  maven:
    - https://www.simplemodeling.org/repository/maven
```

`development.enabled` is the single switch for the launcher and runtime
development checkouts in this file. Set it to `false` to retain the configured
candidate paths while using the installed launcher and selected published
runtime. No shell environment switch is required. Higher-precedence project
launcher configuration may override the global switch.

The launcher and runtime sections can override the common switch independently.
For example, this uses the development Cozy launcher with the published Cozy
runtime while retaining both checkout paths:

```yaml
development:
  enabled: false
  launcher:
    enabled: true
    dev-dir: /Users/asami/src/dev2026/cozy-launcher
  runtime:
    enabled: false
    dev-dir: /Users/asami/src/dev2025/cozy
```

Section `enabled` values take precedence over `development.enabled`.

Direct `launcher.dev-dir`, `runtime.dev-dir`, CLI development-directory options,
and their direct environment equivalents remain explicit always-active
overrides. In normal operation, `launcher.yaml` is the single place that
controls development selection; CLI and environment overrides are reserved for
explicit emergency control. Use `development.*` for paths controlled by the
file switch.

When an effective launcher or runtime development switch is `true`, its
corresponding `dev-dir` is required. A direct environment directory also
satisfies this configuration-time requirement; CLI options may override a valid
selection when the command is processed. Missing directories fail during
launcher configuration instead of silently selecting a published implementation.

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
