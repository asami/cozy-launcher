package cozy.launcher

import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.nio.file.{Files, Path}

/*
 * @since   Jun.  9, 2026
 * @version Jun. 27, 2026
 * @author  ASAMI, Tomoharu
 */
object CozyLauncherSpec {
  def main(args: Array[String]): Unit = {
    val spec = new CozyLauncherSpec
    spec.parser()
    spec.runtimeVersion()
    spec.runtimeCurrentUsesDevelopmentRuntime()
    spec.launcherVersion()
    spec.runtimeHelp()
    spec.configMerge()
    spec.configFileOptionOverridesProjectConfig()
    spec.workspaceRootConfigAppliesToNestedCwd()
    spec.environmentSelectsDevelopmentRuntime()
    spec.launcherDevelopmentBootstrapUsesDevelopmentRuntime()
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
    spec.developmentInvokersUseJavaDirect()
    spec.noRuntimeLibraryDependencies()
    println("CozyLauncherSpec: OK")
  }
}

final class CozyLauncherSpec extends AnyWordSpec with Matchers with GivenWhenThen {
  "cozy launcher" should {
    "command parsing" which {
      "parser" in {
        Given("the cozy launcher scenario: parser")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        parser()
      }

    }

    "configuration and launcher metadata" which {
      "launcher version" in {
        Given("the cozy launcher scenario: launcher version")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        launcherVersion()
      }

      "config merge" in {
        Given("the cozy launcher scenario: config merge")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        configMerge()
      }

      "config file option overrides project config" in {
        Given("the cozy launcher scenario: config file option overrides project config")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        configFileOptionOverridesProjectConfig()
      }

      "workspace root config applies to nested cwd" in {
        Given("the cozy launcher scenario: workspace root config applies to nested cwd")
        When("the launcher loads config from a nested scripted fixture directory")
        Then("the executable specification holds through inherited root config")
        workspaceRootConfigAppliesToNestedCwd()
      }

      "execute uses configured runtime development directory" in {
        Given("the cozy launcher scenario: execute uses configured runtime development directory")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        executeUsesConfiguredRuntimeDevelopmentDirectory()
      }

      "environment selects development runtime" in {
        environmentSelectsDevelopmentRuntime()
      }

      "launcher development bootstrap uses development runtime" in {
        launcherDevelopmentBootstrapUsesDevelopmentRuntime()
      }

      "launcher dev dir delegates to development launcher" in {
        Given("the cozy launcher scenario: launcher dev dir delegates to development launcher")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        launcherDevDirDelegatesToDevelopmentLauncher()
      }

    }

    "runtime selection and catalog operations" which {
      "runtime version" in {
        Given("the cozy launcher scenario: runtime version")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        runtimeVersion()
      }

      "runtime current uses development runtime" in {
        Given("a Cozy project config points to a runtime development checkout")
        When("runtime current is requested")
        Then("the launcher reports the development runtime version from build.sbt")
        runtimeCurrentUsesDevelopmentRuntime()
      }

      "runtime help" in {
        Given("the cozy launcher scenario: runtime help")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        runtimeHelp()
      }

      "runtime catalog selection" in {
        Given("the cozy launcher scenario: runtime catalog selection")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        runtimeCatalogSelection()
      }

      "runtime catalog commands" in {
        Given("the cozy launcher scenario: runtime catalog commands")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        runtimeCatalogCommands()
      }

      "runtime current warns when cached recommended is stale" in {
        Given("the cozy launcher scenario: runtime current warns when cached recommended is stale")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        runtimeCurrentWarnsWhenCachedRecommendedIsStale()
      }

      "runtime version precedence" in {
        Given("the cozy launcher scenario: runtime version precedence")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        runtimeVersionPrecedence()
      }

      "runtime use writes expected files" in {
        Given("the cozy launcher scenario: runtime use writes expected files")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        runtimeUseWritesExpectedFiles()
      }

      "runtime use auto selects project when cozy directory exists" in {
        Given("the cozy launcher scenario: runtime use auto selects project when cozy directory exists")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        runtimeUseAutoSelectsProjectWhenCozyDirectoryExists()
      }

    }

    "artifact execution and resolution" which {
      "execute delegates to cozy runtime" in {
        Given("the cozy launcher scenario: execute delegates to cozy runtime")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        executeDelegatesToCozyRuntime()
      }

      "execute uses cli runtime development directory" in {
        Given("the cozy launcher scenario: execute uses cli runtime development directory")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        executeUsesCliRuntimeDevelopmentDirectory()
      }

    }

    "development runtime operations" which {
      "development invokers use java direct" in {
        Given("the cozy launcher scenario: development invokers use java direct")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        developmentInvokersUseJavaDirect()
      }

    }

    "packaging boundaries" which {
      "no runtime library dependencies" in {
        Given("the cozy launcher scenario: no runtime library dependencies")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        noRuntimeLibraryDependencies()
      }

    }

  }

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

  def runtimeVersion(): Unit = _with_temp_paths { paths =>
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker)

    val (code, output) = _capture_stdout {
      launcher.run(Vector("version"))
    }

    _assert_equals(code, 0)
    _assert_equals(output.trim, "")
    _assert_equals(resolver.resolvedClasspaths, Vector("recommended"))
    _assert_equals(invoker.lastArgs, Vector("version"))
    _assert_equals(CozyCommandParser.parse(Vector("version")), CozyCommand.Execute(Vector("version"), None, None))
    _assert_equals(CozyCommandParser.parse(Vector("--version")), CozyCommand.Execute(Vector("version"), None, None))
  }

  def runtimeCurrentUsesDevelopmentRuntime(): Unit = _with_temp_paths { paths =>
    Given("a runtime development checkout declares its version in build.sbt")
    val runtime = paths.cwd.resolve("../cozy-runtime").normalize
    _write(runtime.resolve("build.sbt"), "ThisBuild / version := \"9.9.9-SNAPSHOT\"\n")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"), "runtime:\n  devDir: ../cozy-runtime\n")
    val launcher = new CozyLauncher(paths, FakeResolver(), FakeInvoker())

    When("runtime current is executed")
    val (currentcode, currentoutput) = _capture_stdout {
      launcher.run(Vector("runtime", "current"))
    }

    Then("the development runtime version is reported without resolving a published runtime")
    _assert_equals(currentcode, 0)
    _assert_equals(currentoutput.trim, "9.9.9-SNAPSHOT")
  }

  def launcherVersion(): Unit = _with_temp_paths { paths =>
    val launcher = new CozyLauncher(paths, FakeResolver(), FakeInvoker())
    val (code, output) = _capture_stdout {
      launcher.run(Vector("launcher", "version"))
    }
    _assert_equals(code, 0)
    _assert_equals(output.trim, s"${LauncherBuildInfo.name} ${LauncherBuildInfo.version}")
    _assert_equals(CozyCommandParser.parse(Vector("launcher", "version")), CozyCommand.LauncherVersion)
  }

  def runtimeHelp(): Unit = _with_temp_paths { paths =>
    val invoker = FakeInvoker()
    val launcher = new CozyLauncher(paths, FakeResolver(), invoker)
    val (code, output) = _capture_stdout {
      launcher.run(Vector("help"))
    }
    _assert_equals(code, 0)
    _assert_equals(invoker.lastArgs, Vector("--help"))
    output.contains("Launcher help:") shouldBe true
    output.contains("cozy launcher help") shouldBe true
    output.contains("[--runtime <version>] [--runtime-dev-dir <dir>]") shouldBe true
    output.contains("ancestor conf/cozy/launcher.yaml and .cozy/launcher.yaml") shouldBe true
    output.contains("runtime.dev-dir is the configuration equivalent of --runtime-dev-dir") shouldBe true
    _assert_equals(CozyCommandParser.parse(Vector("help")), CozyCommand.RuntimeHelp)
    _assert_equals(CozyCommandParser.parse(Vector("--help")), CozyCommand.RuntimeHelp)
    _assert_equals(CozyCommandParser.parse(Vector("launcher", "help")), CozyCommand.LauncherHelp)
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
    config.mavenRepositories.contains("https://global.example/maven") shouldBe true
    config.coursierRepositories.contains("projectRepo") shouldBe true
    config.coursierRepositories.contains("ivy2Local") shouldBe true
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

  def workspaceRootConfigAppliesToNestedCwd(): Unit = _with_temp_paths { paths =>
    val workspace = paths.cwd
    val fixture = workspace.resolve("src").resolve("sbt-test").resolve("cozy").resolve("fixture")
    _write(workspace.resolve(".cozy").resolve("launcher.yaml"),
      """runtime:
        |  version: root
        |  dev-dir: ../cozy-runtime
        |""".stripMargin)
    _write(fixture.resolve(".cozy").resolve("launcher.yaml"),
      """runtime:
        |  version: fixture
        |""".stripMargin)

    val config = LauncherConfig.load(paths.withCwd(fixture))

    _assert_equals(config.runtimeVersion, Some("fixture"))
    _assert_equals(config.runtimeDevDir, Some("../cozy-runtime"))
  }

  def environmentSelectsDevelopmentRuntime(): Unit = _with_temp_paths { paths =>
    Given("a project config contains development runtime and launcher candidates")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """development:
        |  launcher:
        |    dev-dir: ../candidate-launcher
        |  runtime:
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)

    When("the launcher loads config without the development flag")
    val inert = LauncherConfig.load(paths, Vector.empty, Map.empty)

    Then("the development candidates are recorded but not activated")
    _assert_equals(inert.launcherDevDir, None)
    _assert_equals(inert.runtimeDevDir, None)
    _assert_equals(inert.developmentLauncherDevDir, Some("../candidate-launcher"))
    _assert_equals(inert.developmentRuntimeDevDir, Some("../candidate-runtime"))

    When("the launcher loads config with development enabled")
    val active = LauncherConfig.load(paths, Vector.empty, Map("COZY_USE_DEVELOPMENT" -> "true"))

    Then("the development launcher and runtime candidates become active")
    _assert_equals(active.launcherDevDir, Some("../candidate-launcher"))
    _assert_equals(active.runtimeDevDir, Some("../candidate-runtime"))

    When("explicit environment overrides are supplied")
    val env = LauncherConfig.load(paths, Vector.empty, Map(
      "COZY_VERSION" -> "0.2.23-SNAPSHOT",
      "COZY_RUNTIME_DEV_DIR" -> "../env-runtime",
      "COZY_LAUNCHER_DEV_DIR" -> "../env-launcher"
    ))

    Then("explicit runtime and launcher development directories take precedence")
    _assert_equals(env.runtimeVersion, Some("0.2.23-SNAPSHOT"))
    _assert_equals(env.runtimeDevDir, Some("../env-runtime"))
    _assert_equals(env.launcherDevDir, Some("../env-launcher"))

    When("a delegated development launcher executes a runtime command")
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val devinvoker = FakeRuntimeDevInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker, FakeLauncherDevInvoker(), devinvoker, Map(
      "COZY_USE_DEVELOPMENT" -> "true",
      "COZY_LAUNCHER_DEV_DELEGATED" -> "1"
    ))
    launcher.run(Vector("sbt-bridge", "v1"))

    Then("the runtime checkout is invoked without resolving a published runtime artifact")
    _assert_equals(resolver.resolvedClasspaths, Vector.empty)
    _assert_equals(devinvoker.devDir, Some(paths.cwd.resolve("../candidate-runtime").normalize.toAbsolutePath.normalize))
  }

  def launcherDevelopmentBootstrapUsesDevelopmentRuntime(): Unit = _with_temp_paths { paths =>
    Given("a launcher config contains a bootstrap launcher dev dir and a gated runtime dev dir")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """cozy:
        |  launcher:
        |    dev:
        |      dir: ../candidate-launcher
        |development:
        |  runtime:
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)

    When("the launcher bootstrap config is loaded before development delegation")
    val bootstrap = LauncherConfig.load(paths, Vector.empty, Map.empty)

    Then("only the bootstrap launcher dev dir is immediately active")
    _assert_equals(bootstrap.launcherDevDir, Some("../candidate-launcher"))
    _assert_equals(bootstrap.runtimeDevDir, None)

    When("the delegated development launcher executes a Cozy runtime command with development enabled")
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val devinvoker = FakeRuntimeDevInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker, FakeLauncherDevInvoker(), devinvoker, Map(
      "COZY_USE_DEVELOPMENT" -> "true",
      "COZY_LAUNCHER_DEV_DELEGATED" -> "1"
    ))
    launcher.run(Vector("sbt-bridge", "v1"))

    Then("the runtime command uses the configured runtime checkout without resolving a published runtime artifact")
    _assert_equals(resolver.resolvedClasspaths, Vector.empty)
    _assert_equals(invoker.lastArgs, Vector.empty)
    _assert_equals(devinvoker.devDir, Some(paths.cwd.resolve("../candidate-runtime").normalize.toAbsolutePath.normalize))
    _assert_equals(devinvoker.args, Vector("sbt-bridge", "v1"))
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
    showoutput.contains("recommended: 0.2.20") shouldBe true

    val (channelscode, channelsoutput) = _capture_stdout {
      launcher.run(Vector("runtime", "channels"))
    }
    _assert_equals(channelscode, 0)
    channelsoutput.contains("latest-stable: 0.2.20") shouldBe true

    val (listcode, listoutput) = _capture_stdout {
      launcher.run(Vector("runtime", "remote", "list"))
    }
    _assert_equals(listcode, 0)
    listoutput.contains("0.2.21-SNAPSHOT") shouldBe true
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
    stderr.contains("cached Cozy runtime catalog resolves recommended to 0.2.20") shouldBe true
    stderr.contains("remote catalog resolves it to 0.2.21-SNAPSHOT") shouldBe true
    stderr.contains("cozy runtime refresh") shouldBe true
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
    Files.exists(paths.globalVersion) shouldBe false
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
    val devinvoker = FakeRuntimeDevInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker, FakeLauncherDevInvoker(), devinvoker)
    launcher.run(Vector("--runtime-dev-dir", "../cozy", "sbt-bridge", "v1", "--request", "/tmp/request.json"))
    _assert_equals(resolver.resolvedClasspaths, Vector.empty)
    _assert_equals(invoker.lastArgs, Vector.empty)
    _assert_equals(devinvoker.devDir, Some(paths.cwd.resolve("../cozy").normalize.toAbsolutePath.normalize))
    _assert_equals(devinvoker.args, Vector("sbt-bridge", "v1", "--request", "/tmp/request.json"))
  }

  def executeUsesConfiguredRuntimeDevelopmentDirectory(): Unit = _with_temp_paths { paths =>
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"), "runtime:\n  dev-dir: ../cozy\n")
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val devinvoker = FakeRuntimeDevInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker, FakeLauncherDevInvoker(), devinvoker)
    launcher.run(Vector("sbt-bridge", "v1"))
    _assert_equals(resolver.resolvedClasspaths, Vector.empty)
    _assert_equals(invoker.lastArgs, Vector.empty)
    _assert_equals(devinvoker.devDir, Some(paths.cwd.resolve("../cozy").normalize.toAbsolutePath.normalize))
    _assert_equals(devinvoker.args, Vector("sbt-bridge", "v1"))
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


  def developmentInvokersUseJavaDirect(): Unit = {
    val source = Files.readString(Path.of("src/main/scala/cozy/launcher/CozyRuntimeResolver.scala"))
    val core = Files.readString(Path.of("../goldenport-launcher-core/src/main/scala/org/goldenport/launcher/LauncherDevInvoker.scala"))
    source.contains("runMain cozy.Cozy") shouldBe false
    source.contains("new java.lang.ProcessBuilder(\"sbt\", \"--batch\", \"run\")") shouldBe false
    core.contains("new java.lang.ProcessBuilder(") shouldBe true
    core.contains("\"java\"") shouldBe true
    source.contains("export Runtime / fullClasspath") shouldBe true
  }

  def noRuntimeLibraryDependencies(): Unit = {
    val lines = Files.readString(Path.of("build.sbt")).linesIterator.toVector.map(_.trim)
    def _runtime_library_dependency_(line: String): Boolean =
      line.contains("libraryDependencies") &&
        line.contains("\"") &&
        !line.contains("goldenport-launcher-core") &&
        !line.contains("% Test") &&
        !line.contains("% \"test\"")
    lines.exists(_runtime_library_dependency_) shouldBe false
    lines.exists(_.contains("goldenport-launcher-core")) shouldBe true
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
    actual shouldBe expected

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
