package cozy.launcher

import java.nio.file.{Files, Path}

/*
 * @since   Jun.  9, 2026
 * @version Jun. 10, 2026
 * @author  ASAMI, Tomoharu
 */
object CozyLauncherSpec {
  def main(args: Array[String]): Unit = {
    val spec = new CozyLauncherSpec
    spec.parser()
    spec.launcherVersion()
    spec.configMerge()
    spec.configFileOptionOverridesProjectConfig()
    spec.runtimeCatalogSelection()
    spec.runtimeCatalogCommands()
    spec.runtimeCurrentWarnsWhenCachedRecommendedIsStale()
    spec.runtimeVersionPrecedence()
    spec.runtimeUseWritesExpectedFiles()
    spec.runtimeUseAutoSelectsProjectWhenCozyDirectoryExists()
    spec.executeDelegatesToCozyRuntime()
    spec.executeUsesCliRuntimeDevelopmentDirectory()
    spec.executeUsesConfiguredRuntimeDevelopmentDirectory()
    spec.launcherDevDirDelegatesToDevelopmentLauncher()
    spec.noRuntimeLibraryDependencies()
    println("CozyLauncherSpec: OK")
  }
}

final class CozyLauncherSpec {
  def parser(): Unit = {
    val use = CozyCommandParser.parse(Vector("runtime", "use", "latest"))
      .asInstanceOf[CozyCommand.Runtime.Use]
    _assert_equals(use.version, "latest")
    _assert_equals(use.target, CozyCommand.RuntimeUseTarget.Auto)

    val execute = CozyCommandParser.parse(Vector("--runtime", "0.2.20-SNAPSHOT", "sbt-bridge", "v1"))
      .asInstanceOf[CozyCommand.Execute]
    _assert_equals(execute.runtimeVersion, Some("0.2.20-SNAPSHOT"))
    _assert_equals(execute.runtimeDevDir, None)
    _assert_equals(execute.args, Vector("sbt-bridge", "v1"))

    val dev = CozyCommandParser.parse(Vector("--runtime-dev-dir", "../cozy", "sbt-bridge", "v1"))
      .asInstanceOf[CozyCommand.Execute]
    _assert_equals(dev.runtimeVersion, None)
    _assert_equals(dev.runtimeDevDir, Some("../cozy"))
    _assert_equals(dev.args, Vector("sbt-bridge", "v1"))
  }

  def launcherVersion(): Unit = _with_temp_paths { paths =>
    val launcher = new CozyLauncher(paths, FakeResolver(), FakeInvoker())
    val (code, output) = _capture_stdout {
      launcher.run(Vector("--version"))
    }
    _assert_equals(code, 0)
    _assert_equals(output.trim, s"${LauncherBuildInfo.name} ${LauncherBuildInfo.version}")
    _assert_equals(CozyCommandParser.parse(Vector("version")), CozyCommand.Version)
    _assert_equals(CozyCommandParser.parse(Vector("launcher", "version")), CozyCommand.Version)
  }

  def configMerge(): Unit = _with_temp_paths { paths =>
    _write(paths.cozyHome.resolve("launcher.yaml"),
      """runtime:
        |  version: 0.2.18
        |repositories:
        |  maven:
        |    - https://global.example/maven
        |""".stripMargin)
    _write(paths.cwd.resolve("conf").resolve("cozy").resolve("launcher.yaml"),
      """runtime:
        |  version: 0.2.19
        |repositories:
        |  coursier:
        |    - projectRepo
        |""".stripMargin)
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """cozy:
        |  launcher:
        |    dev:
        |      dir: ../cozy-launcher
        |runtime:
        |  version: 0.2.20-SNAPSHOT
        |  catalog:
        |    url: https://project.example/cozy/runtime-catalog.yaml
        |  dev-dir: ../cozy
        |""".stripMargin)
    val config = LauncherConfig.load(paths)
    _assert_equals(config.launcherDevDir, Some("../cozy-launcher"))
    _assert_equals(config.runtimeVersion, Some("0.2.20-SNAPSHOT"))
    _assert_equals(config.runtimeCatalogUrl, Some("https://project.example/cozy/runtime-catalog.yaml"))
    _assert_equals(config.runtimeDevDir, Some("../cozy"))
    assert(config.mavenRepositories.contains("https://global.example/maven"))
    assert(config.coursierRepositories.contains("projectRepo"))
    assert(config.coursierRepositories.contains("ivy2Local"))
  }

  def configFileOptionOverridesProjectConfig(): Unit = _with_temp_paths { paths =>
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"), "runtime:\n  version: 0.2.19\n")
    _write(paths.cwd.resolve("etc").resolve("launcher.conf"), "runtime.version = 0.2.20-SNAPSHOT\n")
    val launcher = new CozyLauncher(paths, FakeResolver(), FakeInvoker())
    val (code, output) = _capture_stdout {
      launcher.run(Vector("--config", "etc/launcher.conf", "runtime", "current"))
    }
    _assert_equals(code, 0)
    _assert_equals(output.trim, "0.2.20-SNAPSHOT")
  }

  def runtimeCatalogSelection(): Unit = _with_temp_paths { paths =>
    val catalogfile = paths.cwd.resolve("runtime-catalog.yaml")
    _write(catalogfile, _catalog_text)
    val config = LauncherConfig.fromParsed(Map("runtime.catalog.url" -> Vector(catalogfile.toString)))
    val resolver = CoursierCozyRuntimeResolver()
    _assert_equals(resolver.resolveVersion("recommended", config, paths), "0.2.20")
    _assert_equals(resolver.resolveVersion("latest", config, paths), "0.2.20")
    _assert_equals(resolver.resolveVersion("latest-stable", config, paths), "0.2.20")
    _assert_equals(resolver.resolveVersion("latest-snapshot", config, paths), "0.2.21-SNAPSHOT")
    _assert_equals(resolver.resolveVersion("newest", config, paths), "0.2.21-SNAPSHOT")
  }

  def runtimeCatalogCommands(): Unit = _with_temp_paths { paths =>
    val catalogfile = paths.cwd.resolve("runtime-catalog.yaml")
    _write(catalogfile, _catalog_text)
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      s"""runtime:
         |  catalog:
         |    url: $catalogfile
         |""".stripMargin)
    val launcher = new CozyLauncher(paths, FakeResolver(), FakeInvoker())
    val (showcode, showoutput) = _capture_stdout {
      launcher.run(Vector("runtime", "catalog", "show"))
    }
    _assert_equals(showcode, 0)
    assert(showoutput.contains("recommended: 0.2.20"))

    val (channelscode, channelsoutput) = _capture_stdout {
      launcher.run(Vector("runtime", "channels"))
    }
    _assert_equals(channelscode, 0)
    assert(channelsoutput.contains("latest-stable: 0.2.20"))

    val (listcode, listoutput) = _capture_stdout {
      launcher.run(Vector("runtime", "remote", "list"))
    }
    _assert_equals(listcode, 0)
    assert(listoutput.contains("0.2.21-SNAPSHOT"))
  }

  def runtimeCurrentWarnsWhenCachedRecommendedIsStale(): Unit = _with_temp_paths { paths =>
    val remotecatalog = paths.cwd.resolve("runtime-catalog.yaml")
    _write(paths.runtimeCatalog, _catalog_text)
    _write(remotecatalog, _catalog_text.replace("recommended: 0.2.20", "recommended: 0.2.21-SNAPSHOT"))
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      s"""runtime:
         |  catalog:
         |    url: $remotecatalog
         |""".stripMargin)
    val launcher = new CozyLauncher(paths, CoursierCozyRuntimeResolver("false"), FakeInvoker())

    val (code, stdout, stderr) = _capture_stdout_stderr {
      launcher.run(Vector("runtime", "current"))
    }

    _assert_equals(code, 0)
    _assert_equals(stdout.trim, "0.2.20")
    assert(stderr.contains("cached Cozy runtime catalog resolves recommended to 0.2.20"))
    assert(stderr.contains("remote catalog resolves it to 0.2.21-SNAPSHOT"))
    assert(stderr.contains("cozy runtime refresh"))
  }

  def runtimeVersionPrecedence(): Unit = _with_temp_paths { paths =>
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker)
    _write(paths.globalVersion, "0.2.18\n")
    _write(paths.projectVersion, "0.2.19\n")
    launcher.run(Vector("--runtime", "0.2.20-SNAPSHOT", "sbt-bridge", "v1"))
    _assert_equals(resolver.resolvedClasspaths, Vector("0.2.20-SNAPSHOT"))
    _assert_equals(invoker.lastArgs, Vector("sbt-bridge", "v1"))
  }

  def runtimeUseWritesExpectedFiles(): Unit = _with_temp_paths { paths =>
    val launcher = new CozyLauncher(paths, FakeResolver(), FakeInvoker())
    launcher.run(Vector("runtime", "use", "0.2.20-SNAPSHOT", "--global"))
    launcher.run(Vector("runtime", "use", "0.2.19", "--project"))
    _assert_equals(Files.readString(paths.globalVersion).trim, "0.2.20-SNAPSHOT")
    _assert_equals(Files.readString(paths.projectVersion).trim, "0.2.19")
  }

  def runtimeUseAutoSelectsProjectWhenCozyDirectoryExists(): Unit = _with_temp_paths { paths =>
    Files.createDirectories(paths.cwd.resolve(".cozy"))
    val launcher = new CozyLauncher(paths, FakeResolver(), FakeInvoker())
    launcher.run(Vector("runtime", "use", "0.2.20-SNAPSHOT"))
    _assert_equals(Files.readString(paths.projectVersion).trim, "0.2.20-SNAPSHOT")
    assert(!Files.exists(paths.globalVersion))
  }

  def executeDelegatesToCozyRuntime(): Unit = _with_temp_paths { paths =>
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker)
    launcher.run(Vector("sbt-bridge", "v1", "--request", "/tmp/request.json"))
    _assert_equals(resolver.resolvedClasspaths, Vector("recommended"))
    _assert_equals(invoker.lastArgs, Vector("sbt-bridge", "v1", "--request", "/tmp/request.json"))
  }

  def executeUsesCliRuntimeDevelopmentDirectory(): Unit = _with_temp_paths { paths =>
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val devInvoker = FakeRuntimeDevInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker, FakeLauncherDevInvoker(), devInvoker)
    launcher.run(Vector("--runtime-dev-dir", "../cozy", "sbt-bridge", "v1", "--request", "/tmp/request.json"))
    _assert_equals(resolver.resolvedClasspaths, Vector.empty)
    _assert_equals(invoker.lastArgs, Vector.empty)
    _assert_equals(devInvoker.devDir, Some(paths.cwd.resolve("../cozy").normalize.toAbsolutePath.normalize))
    _assert_equals(devInvoker.args, Vector("sbt-bridge", "v1", "--request", "/tmp/request.json"))
  }

  def executeUsesConfiguredRuntimeDevelopmentDirectory(): Unit = _with_temp_paths { paths =>
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"), "runtime:\n  dev-dir: ../cozy\n")
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val devInvoker = FakeRuntimeDevInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker, FakeLauncherDevInvoker(), devInvoker)
    launcher.run(Vector("sbt-bridge", "v1"))
    _assert_equals(resolver.resolvedClasspaths, Vector.empty)
    _assert_equals(invoker.lastArgs, Vector.empty)
    _assert_equals(devInvoker.devDir, Some(paths.cwd.resolve("../cozy").normalize.toAbsolutePath.normalize))
    _assert_equals(devInvoker.args, Vector("sbt-bridge", "v1"))
  }

  def launcherDevDirDelegatesToDevelopmentLauncher(): Unit = _with_temp_paths { paths =>
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """cozy:
        |  launcher:
        |    dev:
        |      dir: ../launcher
        |""".stripMargin)
    val invoker = FakeLauncherDevInvoker()
    val launcher = new CozyLauncher(paths, FakeResolver(), FakeInvoker(), invoker)
    launcher.run(Vector("launcher", "version"))
    _assert_equals(invoker.devDir, Some(paths.cwd.resolve("../launcher").normalize.toAbsolutePath.normalize))
    _assert_equals(invoker.args, Vector("launcher", "version"))
  }

  def noRuntimeLibraryDependencies(): Unit = {
    val build = Files.readString(Path.of("build.sbt"))
    assert(!build.contains("libraryDependencies +="))
    assert(!build.contains("libraryDependencies ++="))
  }

  private def _capture_stdout(f: => Int): (Int, String) = {
    val out = new java.io.ByteArrayOutputStream()
    val code = Console.withOut(new java.io.PrintStream(out)) {
      f
    }
    (code, out.toString)
  }

  private def _capture_stdout_stderr(f: => Int): (Int, String, String) = {
    val out = new java.io.ByteArrayOutputStream()
    val err = new java.io.ByteArrayOutputStream()
    val code = Console.withOut(new java.io.PrintStream(out)) {
      Console.withErr(new java.io.PrintStream(err)) {
        f
      }
    }
    (code, out.toString, err.toString)
  }

  private def _with_temp_paths(f: LauncherPaths => Unit): Unit = {
    val root = Files.createTempDirectory("cozy-launcher-spec-")
    val home = root.resolve("home")
    val cwd = root.resolve("work")
    Files.createDirectories(home)
    Files.createDirectories(cwd)
    f(LauncherPaths(home, cwd))
  }

  private def _write(path: Path, value: String): Unit = {
    Files.createDirectories(path.getParent)
    Files.writeString(path, value)
  }

  private def _assert_equals[A](actual: A, expected: A): Unit =
    assert(actual == expected, s"expected=$expected actual=$actual")

  private val _catalog_text: String =
    """schemaVersion: 1
      |generatedAt: 2026-06-10T00:00:00Z
      |recommended: 0.2.20
      |latestStable: 0.2.20
      |latestSnapshot: 0.2.21-SNAPSHOT
      |mavenRepositories:
      |  - https://example.com/repository/maven
      |coursierRepositories:
      |  - central
      |versions:
      |  - version: 0.2.19
      |    channel: stable
      |    status: active
      |    scalaBinaryVersion: "2.12"
      |    module: org.simplemodeling:cozy_2.12:0.2.19
      |    publishedAt: 2026-06-09T00:00:00Z
      |  - version: 0.2.20
      |    channel: stable
      |    status: active
      |    scalaBinaryVersion: "2.12"
      |    module: org.simplemodeling:cozy_2.12:0.2.20
      |    publishedAt: 2026-06-10T00:00:00Z
      |  - version: 0.2.21-SNAPSHOT
      |    channel: snapshot
      |    status: active
      |    scalaBinaryVersion: "2.12"
      |    module: org.simplemodeling:cozy_2.12:0.2.21-SNAPSHOT
      |    publishedAt: 2026-06-10T01:00:00Z
      |""".stripMargin
}

final class FakeResolver(
  classpath: Option[Vector[Path]] = None
) extends CozyRuntimeResolver {
  var resolvedVersions: Vector[String] = Vector.empty
  var resolvedClasspaths: Vector[String] = Vector.empty
  override def resolveVersion(version: String, config: LauncherConfig, paths: LauncherPaths): String = {
    resolvedVersions :+= version
    version
  }
  def resolve(version: String, config: LauncherConfig, paths: LauncherPaths): Vector[Path] = {
    val concreteversion = resolveVersion(version, config, paths)
    resolvedClasspaths :+= concreteversion
    classpath.getOrElse(Vector(paths.cwd.resolve(s"fake-cozy-$concreteversion.jar")))
  }
}

object FakeResolver {
  def apply(): FakeResolver = new FakeResolver()
  def apply(classpath: Option[Vector[Path]]): FakeResolver = new FakeResolver(classpath)
}

final class FakeInvoker extends CozyInvoker {
  var lastClasspath: Vector[Path] = Vector.empty
  var lastArgs: Vector[String] = Vector.empty

  override def invoke(classpath: Vector[Path], args: Vector[String]): Int = {
    lastClasspath = classpath
    lastArgs = args
    0
  }
}

object FakeInvoker {
  def apply(): FakeInvoker = new FakeInvoker()
}

final class FakeLauncherDevInvoker extends LauncherDevInvoker {
  var devDir: Option[Path] = None
  var args: Vector[String] = Vector.empty

  def invoke(devdir: Path, args: Vector[String]): Int = {
    this.devDir = Some(devdir)
    this.args = args
    0
  }
}

object FakeLauncherDevInvoker {
  def apply(): FakeLauncherDevInvoker = new FakeLauncherDevInvoker()
}

final class FakeRuntimeDevInvoker extends CozyRuntimeDevInvoker {
  var devDir: Option[Path] = None
  var args: Vector[String] = Vector.empty

  def invoke(devdir: Path, args: Vector[String]): Int = {
    this.devDir = Some(devdir)
    this.args = args
    0
  }
}

object FakeRuntimeDevInvoker {
  def apply(): FakeRuntimeDevInvoker = new FakeRuntimeDevInvoker()
}
