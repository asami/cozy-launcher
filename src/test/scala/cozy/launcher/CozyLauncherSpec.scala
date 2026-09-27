package cozy.launcher

import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.nio.file.{Files, Path}
import java.nio.file.attribute.FileTime

/*
 * @since   Jun.  9, 2026
 *  version Jul. 13, 2026
 *  version Aug. 30, 2026
 * @version Sep. 27, 2026
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
    spec.configFileDirectoriesRemainAnchoredToTheirSources()
    spec.configFileLoadersAnchorDeclaredDirectories()
    spec.absoluteConfigFileDirectoriesPreserveDeclarations()
    spec.launcherConfigControlsDevelopmentRuntime()
    spec.launcherDevelopmentBootstrapUsesConfiguredRuntime()
    spec.runtimeCatalogSelection()
    spec.cncfVersionSelectsNewestProvenRuntime()
    spec.runtimeSnapshotClasspathRefreshesButReleaseRemainsCached()
    spec.runtimeCatalogCommands()
    spec.runtimeCurrentWarnsWhenCachedRecommendedIsStale()
    spec.runtimeVersionPrecedence()
    spec.projectYamlRuntimeSelectionUsesMatchingDevelopmentRuntime()
    spec.runtimeUseWritesExpectedFiles()
    spec.runtimeUseAutoSelectsProjectWhenCozyDirectoryExists()
    spec.executeDelegatesToCozyRuntime()
    spec.executeUsesCliRuntimeDevelopmentDirectory()
    spec.executeExplicitRuntimeVersionOverridesConfiguredDevelopmentRuntime()
    spec.executeUsesConfiguredRuntimeDevelopmentDirectory()
    spec.launcherDevDirDelegatesToDevelopmentLauncher()
    spec.developmentInvokersUseJavaDirect()
    spec.developmentClasspathRequiresPreparedCurrentCache()
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

      "configuration file directories remain anchored to their sources" in {
        configFileDirectoriesRemainAnchoredToTheirSources()
      }

      "configuration file loaders anchor declared directories" in {
        configFileLoadersAnchorDeclaredDirectories()
      }

      "absolute configuration file directories preserve declarations" in {
        absoluteConfigFileDirectoriesPreserveDeclarations()
      }

      "execute uses configured runtime development directory" in {
        Given("the cozy launcher scenario: execute uses configured runtime development directory")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        executeUsesConfiguredRuntimeDevelopmentDirectory()
      }

      "launcher config controls development runtime" in {
        launcherConfigControlsDevelopmentRuntime()
      }

      "launcher development bootstrap uses configured runtime" in {
        launcherDevelopmentBootstrapUsesConfiguredRuntime()
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

      "CNCF version selects the newest proven Cozy runtime" in {
        Given("a catalog with multiple proven Cozy runtimes for one CNCF version")
        When("the launcher receives that CNCF version without a Cozy override")
        Then("the newest numeric Cozy version is selected and an absent mapping fails closed")
        cncfVersionSelectsNewestProvenRuntime()
      }

      "snapshot runtime classpaths are refreshed while release classpaths remain cached" in {
        Given("a stale snapshot runtime classpath and a cached release runtime classpath")
        When("the resolver resolves both concrete runtime versions")
        Then("the snapshot is fetched again while the release cache is retained")
        runtimeSnapshotClasspathRefreshesButReleaseRemainsCached()
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

      "project.yaml declared runtime uses a matching development snapshot" in {
        Given("a project declares a Cozy version while global development runtime selection is enabled")
        When("the launcher loads the project configuration and executes a runtime command")
        Then("a matching snapshot checkout runs directly and a release uses its published artifact")
        projectYamlRuntimeSelectionUsesMatchingDevelopmentRuntime()
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

      "explicit runtime version overrides configured development runtime" in {
        Given("a global launcher config enables a runtime development candidate")
        When("the launcher executes with an explicit runtime version")
        Then("the requested published runtime is resolved without invoking the candidate checkout")
        executeExplicitRuntimeVersionOverridesConfiguredDevelopmentRuntime()
      }

    }

    "development runtime operations" which {
      "development invokers use java direct" in {
        Given("the cozy launcher scenario: development invokers use java direct")
        When("the launcher behavior is exercised")
        Then("the executable specification holds through scenario-specific expectations")
        developmentInvokersUseJavaDirect()
      }

      "development classpath requires a prepared current cache" in {
        Given("missing or cached development classpaths and build, project, source, and generated inputs")
        When("the launcher evaluates missing, equal, newer relevant, and newer generated timestamps")
        Then("missing or stale classpaths stop execution without launching sbt")
        developmentClasspathRequiresPreparedCurrentCache()
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

    val cncf = CozyCommandParser.parse(Vector("--cozy-for-cncf", "0.5.2", "sbt-bridge", "v1"))
      .asInstanceOf[CozyCommand.Execute]
    _assert_equals(cncf.cncfVersion, Some("0.5.2"))
    _assert_equals(cncf.args, Vector("sbt-bridge", "v1"))

    val generator = CozyCommandParser.parse(Vector("modeler-scala", "model.cml", "--cncf-version", "0.5.2"))
      .asInstanceOf[CozyCommand.Execute]
    _assert_equals(generator.cncfVersion, None)
    _assert_equals(generator.args, Vector("modeler-scala", "model.cml", "--cncf-version", "0.5.2"))
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
    val runtime = paths.cwd.resolve(".cozy").resolve("../cozy-runtime").normalize
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
    output.contains("[--runtime <version>] [--runtime-dev-dir <dir>] [--cozy-for-cncf <version>]") shouldBe true
    output.contains("ancestor conf/cozy/launcher.yaml and .cozy/launcher.yaml") shouldBe true
    output.contains("runtime.dev-dir selects a checkout") shouldBe true
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
    _assert_equals(config.launcherDevDir, Some(paths.cwd.resolve(".cozy").resolve("../cozy-launcher").normalize.toString))
    _assert_equals(config.runtimeVersion, Some("0.2.20-SNAPSHOT"))
    _assert_equals(config.runtimeCatalogUrl, Some("https://project.example/cozy/runtime-catalog.yaml"))
    _assert_equals(config.runtimeDevDir, Some(paths.cwd.resolve(".cozy").resolve("../cozy").normalize.toString))
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
    _assert_equals(config.runtimeDevDir, Some(workspace.resolve(".cozy").resolve("../cozy-runtime").normalize.toString))
  }

  def configFileDirectoriesRemainAnchoredToTheirSources(): Unit = _with_temp_paths { paths =>
    Given("global and workspace configurations declare direct and switchable relative checkout directories")
    val workspace = paths.cwd
    val mediapackage = workspace.resolve("src/main/media/architecture/knowledgehub-component-architecture")
    val globalconfigfile = paths.cozyHome.resolve("launcher.yaml")
    val workspaceconfigfile = workspace.resolve(".cozy").resolve("launcher.yaml")
    _write(globalconfigfile,
      """cozy:
        |  launcher:
        |    dev:
        |      dir: ../global-direct-launcher
        |runtime:
        |  dev-dir: ../global-direct-runtime
        |development:
        |  enabled: true
        |  launcher:
        |    dev-dir: ../global-switchable-launcher
        |  runtime:
        |    dev-dir: ../global-switchable-runtime
        |""".stripMargin)
    _write(workspaceconfigfile,
      """runtime:
        |  dev-dir: ../workspace-direct-runtime
        |development:
        |  runtime:
        |    dev-dir: ../workspace-switchable-runtime
        |""".stripMargin)
    val expectedlauncher = globalconfigfile.getParent.resolve("../global-direct-launcher").normalize.toString
    val expectedruntime = workspaceconfigfile.getParent.resolve("../workspace-direct-runtime").normalize.toString
    val expectedswitchablelauncher = globalconfigfile.getParent.resolve("../global-switchable-launcher").normalize.toString
    val expectedswitchableruntime = workspaceconfigfile.getParent.resolve("../workspace-switchable-runtime").normalize.toString

    When("the root and nested media-package paths load the same ancestor configuration")
    val rootconfig = LauncherConfig.load(paths, Vector.empty, Map.empty)
    val nestedpaths = paths.withCwd(mediapackage)
    val nestedconfig = LauncherConfig.load(nestedpaths, Vector.empty, Map.empty)

    Then("higher selected and lower inherited candidates retain their declaring configuration-file bases")
    _assert_equals(rootconfig.launcherDevDir, Some(expectedlauncher))
    _assert_equals(rootconfig.runtimeDevDir, Some(expectedruntime))
    _assert_equals(rootconfig.developmentLauncherDevDir, Some(expectedswitchablelauncher))
    _assert_equals(rootconfig.developmentRuntimeDevDir, Some(expectedswitchableruntime))
    _assert_equals(nestedconfig.launcherDevDir, Some(expectedlauncher))
    _assert_equals(nestedconfig.runtimeDevDir, Some(expectedruntime))
    _assert_equals(nestedconfig.developmentLauncherDevDir, Some(expectedswitchablelauncher))
    _assert_equals(nestedconfig.developmentRuntimeDevDir, Some(expectedswitchableruntime))

    Given("launcher development delegation is already complete for both runtime invocations")
    val environment = Map("COZY_LAUNCHER_DEV_DELEGATED" -> "1")
    val rootinvoker = FakeRuntimeDevInvoker()
    val nestedinvoker = FakeRuntimeDevInvoker()
    val rootlauncher = new CozyLauncher(paths, FakeResolver(), FakeInvoker(), FakeLauncherDevInvoker(), rootinvoker, environment)
    val nestedlauncher = new CozyLauncher(nestedpaths, FakeResolver(), FakeInvoker(), FakeLauncherDevInvoker(), nestedinvoker, environment)

    When("both locations execute the same Cozy runtime command")
    rootlauncher.run(Vector("sbt-bridge", "v1"))
    nestedlauncher.run(Vector("sbt-bridge", "v1"))

    Then("both invocations select the same configured Cozy checkout")
    _assert_equals(rootinvoker.devDir, Some(Path.of(expectedruntime)))
    _assert_equals(nestedinvoker.devDir, Some(Path.of(expectedruntime)))
    _assert_equals(rootinvoker.args, Vector("sbt-bridge", "v1"))
    _assert_equals(nestedinvoker.args, Vector("sbt-bridge", "v1"))
  }

  def configFileLoadersAnchorDeclaredDirectories(): Unit = _with_temp_paths { paths =>
    Given("a standalone launcher configuration declares each supported relative checkout directory")
    val configfile = paths.cwd.resolve("configuration").resolve("launcher.yaml")
    _write(configfile,
      """launcher:
        |  dev-dir: ../direct-launcher
        |runtime:
        |  dev-dir: ../direct-runtime
        |development:
        |  launcher:
        |    dev-dir: ../switchable-launcher
        |  runtime:
        |    dev-dir: ../switchable-runtime
        |""".stripMargin)
    val expectedlauncher = configfile.getParent.resolve("../direct-launcher").normalize.toString
    val expectedruntime = configfile.getParent.resolve("../direct-runtime").normalize.toString
    val expectedswitchablelauncher = configfile.getParent.resolve("../switchable-launcher").normalize.toString
    val expectedswitchableruntime = configfile.getParent.resolve("../switchable-runtime").normalize.toString

    When("the optional and required configuration file loaders parse the declaration")
    val optional = LauncherConfig.loadFile(configfile)
    val required = LauncherConfig.loadRequiredFile(configfile)

    Then("both loaders anchor direct and switchable checkout directories to the configuration file")
    Vector(optional, required).foreach { config =>
      _assert_equals(config.launcherDevDir, Some(expectedlauncher))
      _assert_equals(config.runtimeDevDir, Some(expectedruntime))
      _assert_equals(config.developmentLauncherDevDir, Some(expectedswitchablelauncher))
      _assert_equals(config.developmentRuntimeDevDir, Some(expectedswitchableruntime))
    }
  }

  def absoluteConfigFileDirectoriesPreserveDeclarations(): Unit = _with_temp_paths { paths =>
    Given("a realistic absolute runtime checkout declaration includes a parent segment and a trailing separator")
    val configfile = paths.cwd.resolve("configuration").resolve("launcher.yaml")
    val absoluteruntime = paths.cwd.toAbsolutePath.toString + java.io.File.separator + "cozy" + java.io.File.separator + ".." + java.io.File.separator + "runtime" + java.io.File.separator
    _write(configfile,
      s"""runtime:
        |  dev-dir: $absoluteruntime
        |""".stripMargin)

    When("the optional configuration file loader reads the declaration")
    val config = LauncherConfig.loadFile(configfile)

    Then("the absolute runtime checkout declaration is preserved exactly")
    config.runtimeDevDir shouldBe Some(absoluteruntime)
  }

  def launcherConfigControlsDevelopmentRuntime(): Unit = _with_temp_paths { paths =>
    Given("a launcher config contains disabled development runtime and launcher candidates")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """development:
        |  enabled: false
        |  launcher:
        |    dev-dir: ../candidate-launcher
        |  runtime:
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)

    When("the launcher loads the disabled development configuration")
    val inert = LauncherConfig.load(paths, Vector.empty, Map.empty)

    Then("the development candidates are recorded but not activated")
    _assert_equals(inert.developmentEnabled, Some(false))
    _assert_equals(inert.launcherDevDir, None)
    _assert_equals(inert.runtimeDevDir, None)
    _assert_equals(inert.developmentLauncherDevDir, Some(paths.cwd.resolve(".cozy").resolve("../candidate-launcher").normalize.toString))
    _assert_equals(inert.developmentRuntimeDevDir, Some(paths.cwd.resolve(".cozy").resolve("../candidate-runtime").normalize.toString))

    When("development.enabled is changed to true in the same launcher config")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """development:
        |  enabled: true
        |  launcher:
        |    dev-dir: ../candidate-launcher
        |  runtime:
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)
    val active = LauncherConfig.load(paths, Vector.empty, Map.empty)

    Then("the development launcher and runtime candidates become active")
    _assert_equals(active.developmentEnabled, Some(true))
    _assert_equals(active.launcherDevDir, Some(paths.cwd.resolve(".cozy").resolve("../candidate-launcher").normalize.toString))
    _assert_equals(active.runtimeDevDir, Some(paths.cwd.resolve(".cozy").resolve("../candidate-runtime").normalize.toString))

    When("the launcher section enables only the development launcher")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """development:
        |  enabled: false
        |  launcher:
        |    enabled: true
        |    dev-dir: ../candidate-launcher
        |  runtime:
        |    enabled: false
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)
    val launcheronly = LauncherConfig.load(paths, Vector.empty, Map.empty)

    Then("the runtime remains published while the launcher delegates to its checkout")
    _assert_equals(launcheronly.launcherDevDir, Some(paths.cwd.resolve(".cozy").resolve("../candidate-launcher").normalize.toString))
    _assert_equals(launcheronly.runtimeDevDir, None)

    When("the runtime section enables only the development runtime")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """development:
        |  enabled: false
        |  launcher:
        |    enabled: false
        |    dev-dir: ../candidate-launcher
        |  runtime:
        |    enabled: true
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)
    val runtimeonly = LauncherConfig.load(paths, Vector.empty, Map.empty)

    Then("the installed launcher selects only the runtime checkout")
    _assert_equals(runtimeonly.launcherDevDir, None)
    _assert_equals(runtimeonly.runtimeDevDir, Some(paths.cwd.resolve(".cozy").resolve("../candidate-runtime").normalize.toString))

    Given("the launcher development switch is enabled without a candidate directory")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """development:
        |  enabled: false
        |  launcher:
        |    enabled: true
        |  runtime:
        |    enabled: false
        |""".stripMargin)

    When("the incomplete launcher configuration is loaded")
    val missinglauncher = intercept[CozyException] {
      LauncherConfig.load(paths, Vector.empty, Map.empty)
    }

    Then("the missing launcher directory is reported deterministically")
    missinglauncher.getMessage should include("development.launcher.dev-dir is required")

    Given("the runtime development switch is enabled without a candidate directory")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """development:
        |  enabled: false
        |  launcher:
        |    enabled: false
        |  runtime:
        |    enabled: true
        |""".stripMargin)

    When("the incomplete runtime configuration is loaded")
    val missingruntime = intercept[CozyException] {
      LauncherConfig.load(paths, Vector.empty, Map.empty)
    }

    Then("the missing runtime directory is reported deterministically")
    missingruntime.getMessage should include("development.runtime.dev-dir is required")

    Given("both development switches are enabled without configured candidate directories")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """development:
        |  enabled: true
        |""".stripMargin)

    When("emergency environment overrides provide both directories")
    val emergency = LauncherConfig.load(paths, Vector.empty, Map(
      "COZY_RUNTIME_DEV_DIR" -> "../emergency-runtime",
      "COZY_LAUNCHER_DEV_DIR" -> "../emergency-launcher"
    ))

    Then("the explicit overrides satisfy the enabled development selections")
    _assert_equals(emergency.runtimeDevDir, Some("../emergency-runtime"))
    _assert_equals(emergency.launcherDevDir, Some("../emergency-launcher"))

    When("the removed environment activation flag is present")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """development:
        |  enabled: false
        |  launcher:
        |    dev-dir: ../candidate-launcher
        |  runtime:
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)
    val legacyenvironment = LauncherConfig.load(paths, Vector.empty, Map("COZY_USE_DEVELOPMENT" -> "true"))

    Then("the file switch remains authoritative")
    _assert_equals(legacyenvironment.launcherDevDir, None)
    _assert_equals(legacyenvironment.runtimeDevDir, None)

    When("a higher-priority project config disables globally enabled development candidates")
    _write(paths.cozyHome.resolve("launcher.yaml"),
      """development:
        |  enabled: true
        |  launcher:
        |    dev-dir: ../global-launcher
        |  runtime:
        |    dev-dir: ../global-runtime
        |""".stripMargin)
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"), "development:\n  enabled: false\n")
    val disabledoverride = LauncherConfig.load(paths, Vector.empty, Map.empty)

    Then("the project switch disables inherited development directories")
    _assert_equals(disabledoverride.launcherDevDir, None)
    _assert_equals(disabledoverride.runtimeDevDir, None)

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

  }

  def launcherDevelopmentBootstrapUsesConfiguredRuntime(): Unit = _with_temp_paths { paths =>
    Given("a launcher config enables a bootstrap launcher dev dir and a development runtime dev dir")
    _write(paths.cwd.resolve(".cozy").resolve("launcher.yaml"),
      """cozy:
        |  launcher:
        |    dev:
        |      dir: ../candidate-launcher
        |development:
        |  enabled: true
        |  runtime:
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)

    When("the launcher bootstrap config is loaded before development delegation")
    val bootstrap = LauncherConfig.load(paths, Vector.empty, Map.empty)

    Then("both configured development checkouts are active before delegation")
    _assert_equals(bootstrap.launcherDevDir, Some(paths.cwd.resolve(".cozy").resolve("../candidate-launcher").normalize.toString))
    _assert_equals(bootstrap.runtimeDevDir, Some(paths.cwd.resolve(".cozy").resolve("../candidate-runtime").normalize.toString))

    When("the delegated development launcher executes a Cozy runtime command")
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val devinvoker = FakeRuntimeDevInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker, FakeLauncherDevInvoker(), devinvoker, Map(
      "COZY_LAUNCHER_DEV_DELEGATED" -> "1"
    ))
    launcher.run(Vector("sbt-bridge", "v1"))

    Then("the runtime command uses the configured runtime checkout without resolving a published runtime artifact")
    _assert_equals(resolver.resolvedClasspaths, Vector.empty)
    _assert_equals(invoker.lastArgs, Vector.empty)
    _assert_equals(devinvoker.devDir, Some(paths.cwd.resolve(".cozy").resolve("../candidate-runtime").normalize.toAbsolutePath.normalize))
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

  def cncfVersionSelectsNewestProvenRuntime(): Unit = _with_temp_paths { paths =>
    val catalogfile = paths.cwd.resolve("runtime-catalog.yaml")
    _write(catalogfile, _catalog_text)
    _write(paths.cozyHome.resolve("launcher.yaml"), s"runtime:\n  version: 9.9.9\n  catalog:\n    url: $catalogfile\n")
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker)

    When("the exact CNCF version is supplied")
    launcher.run(Vector("--cozy-for-cncf", "0.5.2", "sbt-bridge", "v1"))

    Then("the newest proven Cozy version is used instead of the unrelated global runtime")
    resolver.resolvedClasspaths shouldBe Vector("0.2.20")
    invoker.lastArgs shouldBe Vector("sbt-bridge", "v1")
    RuntimeCatalog.compareVersions("0.3.2.10", "0.3.2.2") should be > 0

    When("the exact CNCF version has no cataloged proven runtime")
    val error = intercept[CozyException] {
      launcher.run(Vector("--cozy-for-cncf", "0.5.3-SNAPSHOT", "sbt-bridge", "v1"))
    }

    Then("the launcher requests an explicit override instead of choosing global latest")
    error.message should include("no proven Cozy runtime is cataloged for CNCF")
  }

  def runtimeSnapshotClasspathRefreshesButReleaseRemainsCached(): Unit = _with_temp_paths { paths =>
    val snapshotversion = "0.3.3-SNAPSHOT"
    val releaseversion = "0.3.2"
    val stalecached = paths.cwd.resolve("simplemodeler-1.1.25.jar")
    val freshlyfetched = paths.cwd.resolve("simplemodeler-1.1.26-SNAPSHOT.jar")
    val releasecached = paths.cwd.resolve("cozy-0.3.2.jar")
    val snapshotmetadata = paths.runtimeRoot.resolve(snapshotversion).resolve("classpath.txt")
    val releasemetadata = paths.runtimeRoot.resolve(releaseversion).resolve("classpath.txt")
    val catalogfile = paths.cwd.resolve("runtime-catalog.yaml")
    _write(snapshotmetadata, stalecached.toString + "\n")
    _write(releasemetadata, releasecached.toString + "\n")
    _write(catalogfile, _catalog_text)
    val resolver = CoursierCozyRuntimeResolver(_coursier_fetch_script(paths.cwd, freshlyfetched))
    val config = LauncherConfig(runtimeCatalogUrl = Some(catalogfile.toString))

    When("the resolver resolves a snapshot whose persisted classpath is stale")
    val snapshotclasspath = resolver.resolve(snapshotversion, config, paths)

    Then("the resolver fetches and replaces the snapshot classpath")
    snapshotclasspath shouldBe Vector(freshlyfetched)
    Files.readString(snapshotmetadata).trim shouldBe freshlyfetched.toString

    When("the resolver resolves an immutable release whose classpath is cached")
    val releaseclasspath = resolver.resolve(releaseversion, config, paths)

    Then("the release classpath remains reusable without fetching")
    releaseclasspath shouldBe Vector(releasecached)
    Files.readString(releasemetadata).trim shouldBe releasecached.toString
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

  def projectYamlRuntimeSelectionUsesMatchingDevelopmentRuntime(): Unit = _with_temp_paths { paths =>
    Given("a global launcher config enables launcher and runtime development candidates")
    _write(paths.cozyHome.resolve("launcher.yaml"),
      """development:
        |  enabled: true
        |  launcher:
        |    dev-dir: ../candidate-launcher
        |  runtime:
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)
    _write(paths.cwd.resolve("project.yaml"),
      """build:
        |  cozyVersion: "0.3.1"
        |""".stripMargin)

    When("the launcher loads the project runtime selection")
    val environment = Map("COZY_LAUNCHER_DEV_DELEGATED" -> "1")
    val config = LauncherConfig.load(paths, Vector.empty, environment)

    Then("the declaration selects 0.3.1 and retains the configured checkout candidate")
    _assert_equals(config.runtimeVersion, Some("0.3.1"))
    _assert_equals(config.runtimeDevDir, Some(paths.home.resolve("candidate-runtime").toString))

    Given("the launcher is delegated past the selected development launcher checkout")
    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val runtimedevinvoker = FakeRuntimeDevInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker, FakeLauncherDevInvoker(), runtimedevinvoker, environment)

    When("the launcher executes sbt-bridge v1")
    launcher.run(Vector("sbt-bridge", "v1"))

    Then("the release runtime resolves from its published artifact")
    _assert_equals(resolver.resolvedClasspaths, Vector("0.3.1"))
    _assert_equals(invoker.lastArgs, Vector("sbt-bridge", "v1"))
    _assert_equals(runtimedevinvoker.devDir, None)
    _assert_equals(runtimedevinvoker.args, Vector.empty)

    Given("the project and development checkout declare the same snapshot version")
    _write(paths.cwd.resolve("project.yaml"), "build:\n  cozyVersion: 0.3.3-SNAPSHOT\n")
    _write(paths.home.resolve("candidate-runtime").resolve("build.sbt"), "version := \"0.3.3-SNAPSHOT\"\n")
    val snapshotresolver = FakeResolver()
    val snapshotinvoker = FakeInvoker()
    val snapshotdevinvoker = FakeRuntimeDevInvoker()
    val snapshotlauncher = new CozyLauncher(
      paths, snapshotresolver, snapshotinvoker, FakeLauncherDevInvoker(), snapshotdevinvoker, environment
    )

    When("the launcher executes sbt-bridge for that snapshot")
    snapshotlauncher.run(Vector("sbt-bridge", "v1"))

    Then("the Cozy checkout runs directly")
    _assert_equals(snapshotresolver.resolvedClasspaths, Vector.empty)
    _assert_equals(snapshotinvoker.lastArgs, Vector.empty)
    _assert_equals(snapshotdevinvoker.devDir, Some(paths.home.resolve("candidate-runtime")))
    _assert_equals(snapshotdevinvoker.args, Vector("sbt-bridge", "v1"))

    When("an explicit runtime environment version is supplied")
    val overridden = LauncherConfig.load(paths, Vector.empty, environment + ("COZY_RUNTIME_VERSION" -> "0.3.2"))

    Then("the explicit runtime environment version overrides the project declaration")
    _assert_equals(overridden.runtimeVersion, Some("0.3.2"))
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

    Given("the selected checkout declares a different exact Cozy version")
    val checkout = paths.cwd.resolve("../cozy").normalize
    _write(checkout.resolve("build.sbt"), "ThisBuild / version := \"0.3.3-SNAPSHOT\"\n")

    When("an exact runtime version and that checkout are requested together")
    val mismatch = intercept[CozyException] {
      launcher.run(Vector("--runtime", "0.3.4-SNAPSHOT", "--runtime-dev-dir", "../cozy", "sbt-bridge", "v1"))
    }

    Then("the launcher rejects the conflict before using the checkout")
    mismatch.message should include("Cozy development checkout version mismatch")
  }

  def executeExplicitRuntimeVersionOverridesConfiguredDevelopmentRuntime(): Unit = _with_temp_paths { paths =>
    Given("a global launcher config enables a runtime development candidate")
    _write(paths.cozyHome.resolve("launcher.yaml"),
      """development:
        |  enabled: false
        |  runtime:
        |    enabled: true
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)

    val resolver = FakeResolver()
    val invoker = FakeInvoker()
    val devinvoker = FakeRuntimeDevInvoker()
    val launcher = new CozyLauncher(paths, resolver, invoker, FakeLauncherDevInvoker(), devinvoker)

    When("the launcher executes with an explicit runtime version")
    launcher.run(Vector("--runtime", "0.3.2-SNAPSHOT", "sbt-bridge", "v1"))

    Then("the requested published runtime resolves and the candidate checkout is not invoked")
    _assert_equals(resolver.resolvedClasspaths, Vector("0.3.2-SNAPSHOT"))
    _assert_equals(invoker.lastArgs, Vector("sbt-bridge", "v1"))
    _assert_equals(devinvoker.devDir, None)
    _assert_equals(devinvoker.args, Vector.empty)

    Given("a direct runtime.dev-dir and switchable development runtime are configured")
    _write(paths.cozyHome.resolve("launcher.yaml"),
      """runtime:
        |  dev-dir: ../direct-runtime
        |development:
        |  enabled: false
        |  runtime:
        |    enabled: true
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)
    val directresolver = FakeResolver()
    val directinvoker = FakeInvoker()
    val directdevinvoker = FakeRuntimeDevInvoker()
    val directenvironment = Map("COZY_LAUNCHER_DEV_DELEGATED" -> "1")
    val directlauncher = new CozyLauncher(
      paths,
      directresolver,
      directinvoker,
      FakeLauncherDevInvoker(),
      directdevinvoker,
      directenvironment
    )

    When("the launcher executes with an explicit runtime version")
    directlauncher.run(Vector("--runtime", "0.3.2-SNAPSHOT", "sbt-bridge", "v1"))

    Then("the explicit runtime version bypasses direct and switchable checkout selectors")
    _assert_equals(directresolver.resolvedClasspaths, Vector("0.3.2-SNAPSHOT"))
    _assert_equals(directinvoker.lastArgs, Vector("sbt-bridge", "v1"))
    _assert_equals(directdevinvoker.devDir, None)
    _assert_equals(directdevinvoker.args, Vector.empty)

    Given("a runtime environment checkout selector and switchable development runtime are configured")
    _write(paths.cozyHome.resolve("launcher.yaml"),
      """development:
        |  enabled: false
        |  runtime:
        |    enabled: true
        |    dev-dir: ../candidate-runtime
        |""".stripMargin)
    val environmentresolver = FakeResolver()
    val environmentinvoker = FakeInvoker()
    val environmentdevinvoker = FakeRuntimeDevInvoker()
    val environment = Map(
      "COZY_LAUNCHER_DEV_DELEGATED" -> "1",
      "COZY_RUNTIME_DEV_DIR" -> "../environment-runtime"
    )
    val environmentlauncher = new CozyLauncher(
      paths,
      environmentresolver,
      environmentinvoker,
      FakeLauncherDevInvoker(),
      environmentdevinvoker,
      environment
    )

    When("the launcher executes with an explicit runtime version")
    environmentlauncher.run(Vector("--runtime", "0.3.2-SNAPSHOT", "sbt-bridge", "v1"))

    Then("the explicit runtime version bypasses the environment checkout selector")
    _assert_equals(environmentresolver.resolvedClasspaths, Vector("0.3.2-SNAPSHOT"))
    _assert_equals(environmentinvoker.lastArgs, Vector("sbt-bridge", "v1"))
    _assert_equals(environmentdevinvoker.devDir, None)
    _assert_equals(environmentdevinvoker.args, Vector.empty)

    Given("both explicit runtime selectors are supplied")
    val explicitresolver = FakeResolver()
    val explicitinvoker = FakeInvoker()
    val explicitdevinvoker = FakeRuntimeDevInvoker()
    val explicitlauncher = new CozyLauncher(
      paths,
      explicitresolver,
      explicitinvoker,
      FakeLauncherDevInvoker(),
      explicitdevinvoker
    )

    When("the launcher executes with an explicit runtime development directory")
    explicitlauncher.run(Vector(
      "--runtime", "0.3.2-SNAPSHOT",
      "--runtime-dev-dir", "../explicit-runtime",
      "sbt-bridge", "v1"
    ))

    Then("the explicit development directory wins over the runtime version")
    _assert_equals(explicitresolver.resolvedClasspaths, Vector.empty)
    _assert_equals(explicitinvoker.lastArgs, Vector.empty)
    _assert_equals(
      explicitdevinvoker.devDir,
      Some(paths.cwd.resolve("../explicit-runtime").normalize.toAbsolutePath.normalize)
    )
    _assert_equals(explicitdevinvoker.args, Vector("sbt-bridge", "v1"))

    Given("the configured checkout declares the requested snapshot version")
    _write(paths.home.resolve("candidate-runtime").resolve("build.sbt"), "version := \"0.3.2-SNAPSHOT\"\n")
    val matchingresolver = FakeResolver()
    val matchinginvoker = FakeInvoker()
    val matchingdevinvoker = FakeRuntimeDevInvoker()
    val matchinglauncher = new CozyLauncher(
      paths, matchingresolver, matchinginvoker, FakeLauncherDevInvoker(), matchingdevinvoker
    )

    When("the launcher receives that explicit runtime version")
    matchinglauncher.run(Vector("--runtime", "0.3.2-SNAPSHOT", "sbt-bridge", "v1"))

    Then("the version-matched checkout runs directly")
    _assert_equals(matchingresolver.resolvedClasspaths, Vector.empty)
    _assert_equals(matchinginvoker.lastArgs, Vector.empty)
    _assert_equals(matchingdevinvoker.devDir, Some(paths.home.resolve("candidate-runtime")))
    _assert_equals(matchingdevinvoker.args, Vector("sbt-bridge", "v1"))

    Given("the configured checkout has a different snapshot version")
    val mismatchingresolver = FakeResolver()
    val mismatchinginvoker = FakeInvoker()
    val mismatchingdevinvoker = FakeRuntimeDevInvoker()
    val mismatchinglauncher = new CozyLauncher(
      paths, mismatchingresolver, mismatchinginvoker, FakeLauncherDevInvoker(), mismatchingdevinvoker
    )

    When("the launcher receives another explicit snapshot version")
    mismatchinglauncher.run(Vector("--runtime", "0.3.4-SNAPSHOT", "sbt-bridge", "v1"))

    Then("the requested artifact resolves without running the mismatched checkout")
    _assert_equals(mismatchingresolver.resolvedClasspaths, Vector("0.3.4-SNAPSHOT"))
    _assert_equals(mismatchinginvoker.lastArgs, Vector("sbt-bridge", "v1"))
    _assert_equals(mismatchingdevinvoker.devDir, None)
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
    _assert_equals(devinvoker.devDir, Some(paths.cwd.resolve(".cozy").resolve("../cozy").normalize.toAbsolutePath.normalize))
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
    _assert_equals(invoker.devDir, Some(paths.cwd.resolve(".cozy").resolve("../launcher").normalize.toAbsolutePath.normalize))
    _assert_equals(invoker.args, Vector("launcher", "version"))
  }


  def developmentInvokersUseJavaDirect(): Unit = {
    val source = Files.readString(Path.of("src/main/scala/cozy/launcher/CozyRuntimeResolver.scala"))
    val core = Files.readString(Path.of("../goldenport-launcher-core/src/main/scala/org/goldenport/launcher/LauncherDevInvoker.scala"))
    source.contains("runMain cozy.Cozy") shouldBe false
    source.contains("new java.lang.ProcessBuilder(\"sbt\", \"--batch\", \"run\")") shouldBe false
    core.contains("new java.lang.ProcessBuilder(") shouldBe true
    core.contains("\"java\"") shouldBe true
    source.contains("SbtRuntimeClasspathExporter") shouldBe false
  }

  def developmentClasspathRequiresPreparedCurrentCache(): Unit = {
    val basetime = FileTime.fromMillis(1_800_000_000_000L)
    val oldtime = FileTime.fromMillis(basetime.toMillis - 1000L)
    val newtime = FileTime.fromMillis(basetime.toMillis + 1000L)

    Given("a development checkout without a prepared classpath")
    val missingproject = _development_project()
    val missingcache = DevelopmentClasspath.classpathFile(missingproject)

    When("the development classpath is requested")
    val missingerror = intercept[CozyException] {
      DevelopmentClasspath.classpath(missingproject)
    }

    Then("the launcher stops and names the explicit preparation task")
    missingerror.message should include("development classpath missing")
    missingerror.message should include("cozyExportRuntimeClasspath")
    Files.exists(missingcache) shouldBe false

    Given("an empty prepared classpath file")
    val emptyproject = _development_project()
    _write(DevelopmentClasspath.classpathFile(emptyproject), "")

    When("the development classpath is requested")
    val emptyerror = intercept[CozyException] {
      DevelopmentClasspath.classpath(emptyproject)
    }

    Then("the launcher treats the empty file as missing")
    emptyerror.message should include("development classpath missing")

    Given("a build.sbt input with the same timestamp as its cached classpath")
    val equalproject = _development_project()
    val equalcache = DevelopmentClasspath.classpathFile(equalproject)
    _write(equalcache, equalproject.resolve("cached.jar").toString)
    Files.setLastModifiedTime(equalcache, basetime)
    Files.setLastModifiedTime(equalproject.resolve("build.sbt"), basetime)

    When("the development classpath is requested")
    val equalerror = intercept[CozyException] {
      DevelopmentClasspath.classpath(equalproject)
    }

    Then("the equal timestamp causes a stale-cache failure")
    equalerror.message should include("development classpath stale")

    Given("a project definition newer than its cached classpath")
    val projectinput = _development_project()
    val projectcache = DevelopmentClasspath.classpathFile(projectinput)
    _write(projectcache, projectinput.resolve("cached.jar").toString)
    Files.setLastModifiedTime(projectinput.resolve("build.sbt"), oldtime)
    Files.setLastModifiedTime(projectinput.resolve("src/main/scala/Main.scala"), oldtime)
    Files.setLastModifiedTime(projectcache, basetime)
    Files.setLastModifiedTime(projectinput.resolve("project/plugins.sbt"), newtime)

    When("the development classpath is requested")
    val projecterror = intercept[CozyException] {
      DevelopmentClasspath.classpath(projectinput)
    }

    Then("the project input causes a stale-cache failure")
    projecterror.message should include("development classpath stale")

    Given("a runtime source newer than its cached classpath")
    val sourceproject = _development_project()
    val sourcecache = DevelopmentClasspath.classpathFile(sourceproject)
    _write(sourcecache, sourceproject.resolve("cached.jar").toString)
    Files.setLastModifiedTime(sourceproject.resolve("build.sbt"), oldtime)
    Files.setLastModifiedTime(sourceproject.resolve("project/plugins.sbt"), oldtime)
    Files.setLastModifiedTime(sourcecache, basetime)
    Files.setLastModifiedTime(sourceproject.resolve("src/main/scala/Main.scala"), newtime)

    When("the development classpath is requested")
    val sourceerror = intercept[CozyException] {
      DevelopmentClasspath.classpath(sourceproject)
    }

    Then("the runtime source causes a stale-cache failure")
    sourceerror.message should include("development classpath stale")

    Given("only a generated build-directory file is newer than the cached classpath")
    val generatedproject = _development_project()
    val generatedcache = DevelopmentClasspath.classpathFile(generatedproject)
    val generatedfile = generatedproject.resolve("src/main/target/Generated.scala")
    _write(generatedcache, generatedproject.resolve("cached.jar").toString)
    _write(generatedfile, "object Generated\n")
    Files.setLastModifiedTime(generatedproject.resolve("build.sbt"), oldtime)
    Files.setLastModifiedTime(generatedproject.resolve("project/plugins.sbt"), oldtime)
    Files.setLastModifiedTime(generatedproject.resolve("src/main/scala/Main.scala"), oldtime)
    Files.setLastModifiedTime(generatedcache, basetime)
    Files.setLastModifiedTime(generatedfile, newtime)

    When("the development classpath is requested")
    val generatedclasspath = DevelopmentClasspath.classpath(generatedproject)

    Then("the generated file is ignored and the cached classpath is retained")
    generatedclasspath shouldBe Vector(generatedproject.resolve("cached.jar"))
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

  private def _development_project(): Path = {
    val project = Files.createTempDirectory("cozy-launcher-development-classpath-spec-")
    _write(project.resolve("build.sbt"), "ThisBuild / version := \"0.1.7-SNAPSHOT\"\n")
    _write(project.resolve("project/plugins.sbt"), "// project input\n")
    _write(project.resolve("src/main/scala/Main.scala"), "object Main\n")
    project
  }

  private def _write(path: Path, value: String): Unit = {
    Files.createDirectories(path.getParent)
    Files.writeString(path, value)
  }

  private def _coursier_fetch_script(directory: Path, classpath: Path): String = {
    val script = directory.resolve("fake-coursier")
    _write(script, s"#!/bin/sh\nprintf '%s\\n' '${classpath}'\n")
    script.toFile.setExecutable(true) shouldBe true
    script.toString
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
      |    cncfVersions: 0.5.2
      |    scalaBinaryVersion: "2.12"
      |    module: org.simplemodeling:cozy_2.12:0.2.19
      |    publishedAt: 2026-06-09T00:00:00Z
      |  - version: 0.2.20
      |    channel: stable
      |    status: active
      |    cncfVersions: 0.5.2
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
