package cozy.launcher

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import org.goldenport.launcher.{LauncherConfigLoader => CoreLauncherConfigLoader, LauncherConfigParser => CoreLauncherConfigParser, LauncherConfigSource => CoreLauncherConfigSource, LauncherCoreException, LauncherPaths => CoreLauncherPaths, LauncherProductSpec}

/*
 * @since   Jun.  9, 2026
 *  version Jul. 13, 2026
 * @version Aug. 29, 2026
 * @author  ASAMI, Tomoharu
 */
final case class LauncherConfig(
  launcherDevDir: Option[String] = None,
  runtimeVersion: Option[String] = None,
  runtimeCatalogUrl: Option[String] = None,
  runtimeDevDir: Option[String] = None,
  developmentEnabled: Option[Boolean] = None,
  developmentLauncherEnabled: Option[Boolean] = None,
  developmentRuntimeEnabled: Option[Boolean] = None,
  developmentLauncherDevDir: Option[String] = None,
  developmentRuntimeDevDir: Option[String] = None,
  mavenRepositories: Vector[String] = Vector.empty,
  coursierRepositories: Vector[String] = Vector.empty
) {
  def mergeHigher(higher: LauncherConfig): LauncherConfig =
    LauncherConfig(
      launcherDevDir = higher.launcherDevDir.orElse(launcherDevDir),
      runtimeVersion = higher.runtimeVersion.orElse(runtimeVersion),
      runtimeCatalogUrl = higher.runtimeCatalogUrl.orElse(runtimeCatalogUrl),
      runtimeDevDir = higher.runtimeDevDir.orElse(runtimeDevDir),
      developmentEnabled = higher.developmentEnabled.orElse(developmentEnabled),
      developmentLauncherEnabled = higher.developmentLauncherEnabled.orElse(developmentLauncherEnabled),
      developmentRuntimeEnabled = higher.developmentRuntimeEnabled.orElse(developmentRuntimeEnabled),
      developmentLauncherDevDir = higher.developmentLauncherDevDir.orElse(developmentLauncherDevDir),
      developmentRuntimeDevDir = higher.developmentRuntimeDevDir.orElse(developmentRuntimeDevDir),
      mavenRepositories = _merge_list(mavenRepositories, higher.mavenRepositories),
      coursierRepositories = _merge_list(coursierRepositories, higher.coursierRepositories)
    )

  def withCatalog(catalog: RuntimeCatalog): LauncherConfig =
    copy(
      mavenRepositories = _merge_catalog_list(mavenRepositories, catalog.mavenRepositories, LauncherConfig.DEFAULT_MAVEN_REPOSITORIES),
      coursierRepositories = _merge_catalog_list(coursierRepositories, catalog.coursierRepositories, LauncherConfig.DEFAULT_COURSIER_REPOSITORIES)
    )

  def isDevelopmentLauncherEnabled: Boolean =
    developmentLauncherEnabled.orElse(developmentEnabled).contains(true)

  def isDevelopmentRuntimeEnabled: Boolean =
    developmentRuntimeEnabled.orElse(developmentEnabled).contains(true)

  def withDevelopmentSelection: LauncherConfig =
    copy(
      launcherDevDir = _select_development_directory(
        isDevelopmentLauncherEnabled,
        launcherDevDir,
        developmentLauncherDevDir,
        "development.launcher.dev-dir"
      ),
      runtimeDevDir = _select_development_directory(
        isDevelopmentRuntimeEnabled,
        runtimeDevDir,
        developmentRuntimeDevDir,
        "development.runtime.dev-dir"
      )
    )

  private def _select_development_directory(
    enabled: Boolean,
    direct: Option[String],
    candidate: Option[String],
    configkey: String
  ): Option[String] =
    if (enabled)
      direct.orElse(candidate).orElse(throw CozyException(s"$configkey is required when its development switch is enabled"))
    else
      direct

  def normalizedWithDefaults: LauncherConfig =
    copy(
      mavenRepositories = _append_defaults(mavenRepositories, LauncherConfig.DEFAULT_MAVEN_REPOSITORIES),
      coursierRepositories = _append_defaults(coursierRepositories, LauncherConfig.DEFAULT_COURSIER_REPOSITORIES)
    )

  private def _merge_list(
    lower: Vector[String],
    higher: Vector[String]
  ): Vector[String] =
    (higher ++ lower).distinct

  private def _merge_catalog_list(
    explicit: Vector[String],
    catalog: Vector[String],
    defaults: Vector[String]
  ): Vector[String] =
    (explicit ++ catalog ++ defaults).distinct

  private def _append_defaults(
    configured: Vector[String],
    defaults: Vector[String]
  ): Vector[String] =
    configured ++ defaults.filterNot(configured.contains)
}

object LauncherConfig {
  val DEFAULT_RUNTIME_VERSION = "recommended"
  val DEFAULT_RUNTIME_CATALOG_URL = "https://www.simplemodeling.org/repository/cozy/runtime-catalog.yaml"
  val DEFAULT_MAVEN_REPOSITORIES = Vector(
    "https://www.simplemodeling.org/repository/maven",
    "https://raw.github.com/asami/maven-repository/2020/releases",
    "https://raw.github.com/asami/maven-repository/2025/releases",
    "https://maven.pkg.github.com/asami/maven-repository"
  )
  val DEFAULT_COURSIER_REPOSITORIES = Vector("ivy2Local", "central")

  private val _product_spec = LauncherProductSpec.hiddenLauncherYaml("cozy", "COZY")

  def load(paths: LauncherPaths): LauncherConfig =
    load(paths, Vector.empty)

  def load(
    paths: LauncherPaths,
    configfiles: Vector[String]
  ): LauncherConfig =
    load(paths, configfiles, sys.env)

  def load(
    paths: LauncherPaths,
    configfiles: Vector[String],
    environment: Map[String, String]
  ): LauncherConfig = {
    val corepaths = CoreLauncherPaths(home = paths.home, cwd = paths.cwd)
    val explicit =
      try {
        CoreLauncherConfigLoader.load(corepaths, _product_spec, configfiles).foldLeft(LauncherConfig()) { (acc, source) =>
          acc.mergeHigher(_from_config_source(source))
        }
      } catch {
        case e: LauncherCoreException => throw CozyException(e.getMessage, e.code)
      }
    val projectselection =
      try {
        _project_runtime_selection(paths)
      } catch {
        case e: LauncherCoreException => throw CozyException(e.getMessage, e.code)
      }
    val withprojectselection = explicit.mergeHigher(projectselection)
    val withoverrides = withprojectselection.mergeHigher(fromEnvironment(environment))
    withoverrides.withDevelopmentSelection.normalizedWithDefaults
  }

  private def _project_runtime_selection(paths: LauncherPaths): LauncherConfig =
    _nearest_project_yaml(paths).flatMap { path =>
      val values = CoreLauncherConfigParser.parse(path, Files.readString(path, StandardCharsets.UTF_8))
      values.getOrElse("build.cozyVersion", Vector.empty).map(_.trim).find(_.nonEmpty)
    }.fold(LauncherConfig()) { version =>
      LauncherConfig(
        runtimeVersion = Some(version),
        developmentRuntimeEnabled = Some(false)
      )
    }

  private def _nearest_project_yaml(paths: LauncherPaths): Option[Path] = {
    val cwd = paths.cwd.toAbsolutePath.normalize
    val home = paths.home.toAbsolutePath.normalize
    Iterator.iterate(Option(cwd))(_.flatMap(p => Option(p.getParent))).
      takeWhile(_.nonEmpty).
      flatten.
      takeWhile(p => p != home.getParent).
      map(_.resolve("project.yaml")).
      find(Files.isRegularFile(_))
  }

  def loadFile(path: Path): LauncherConfig =
    if (Files.isRegularFile(path)) {
      val text = Files.readString(path, StandardCharsets.UTF_8)
      _from_config_file(path, text)
    } else {
      LauncherConfig()
    }

  def loadRequiredFile(path: Path): LauncherConfig =
    if (Files.isRegularFile(path))
      loadFile(path)
    else
      throw CozyException(s"launcher config file not found: ${path}")

  def fromParsed(values: Map[String, Vector[String]]): LauncherConfig = {
    def _first_(keys: String*): Option[String] =
      keys.toVector.flatMap(k => values.getOrElse(k, Vector.empty)).headOption.map(_.trim).filter(_.nonEmpty)
    def _all_(keys: String*): Vector[String] =
      keys.toVector.flatMap(k => values.getOrElse(k, Vector.empty)).map(_.trim).filter(_.nonEmpty).distinct
    def _boolean_(keys: String*): Option[Boolean] =
      _first_(keys*).map {
        case "true" | "yes" | "on" | "1" => true
        case "false" | "no" | "off" | "0" => false
        case other => throw CozyException(s"invalid boolean config value: $other")
      }

    LauncherConfig(
      launcherDevDir = _first_("cozy.launcher.dev.dir", "cozy.launcher.dev-dir", "cozy.launcher.devDir", "launcher.dev.dir", "launcher.dev-dir", "launcher.devDir"),
      runtimeVersion = _first_("runtime.version", "cozy.runtime.version", "version"),
      runtimeCatalogUrl = _first_("runtime.catalog.url", "cozy.runtime.catalog.url", "catalog.url"),
      runtimeDevDir = _first_("runtime.dev-dir", "runtime.dev_dir", "runtime.devDir", "runtime.dev.dir", "cozy.runtime.dev-dir", "cozy.runtime.dev_dir", "cozy.runtime.devDir", "cozy.runtime.dev.dir"),
      developmentEnabled = _boolean_("development.enabled", "cozy.development.enabled"),
      developmentLauncherEnabled = _boolean_("development.launcher.enabled", "cozy.development.launcher.enabled"),
      developmentRuntimeEnabled = _boolean_("development.runtime.enabled", "cozy.development.runtime.enabled"),
      developmentLauncherDevDir = _first_("development.launcher.dev-dir", "development.launcher.dev_dir", "development.launcher.devDir", "development.launcher.dev.dir", "cozy.development.launcher.dev-dir", "cozy.development.launcher.dev_dir", "cozy.development.launcher.devDir", "cozy.development.launcher.dev.dir"),
      developmentRuntimeDevDir = _first_("development.runtime.dev-dir", "development.runtime.dev_dir", "development.runtime.devDir", "development.runtime.dev.dir", "cozy.development.runtime.dev-dir", "cozy.development.runtime.dev_dir", "cozy.development.runtime.devDir", "cozy.development.runtime.dev.dir"),
      mavenRepositories = _all_("repositories.maven", "cozy.repository.maven"),
      coursierRepositories = _all_("repositories.coursier", "cozy.repository.coursier")
    )
  }

  def fromEnvironment(environment: Map[String, String] = sys.env): LauncherConfig = {
    LauncherConfig(
      launcherDevDir = _env_first(environment, "COZY_LAUNCHER_DEV_DIR"),
      runtimeVersion = _env_first(environment, "COZY_RUNTIME_VERSION", "COZY_VERSION"),
      runtimeDevDir = _env_first(environment, "COZY_RUNTIME_DEV_DIR")
    )
  }

  private def _from_config_source(source: CoreLauncherConfigSource): LauncherConfig =
    _from_config_file(source.path, source.values)

  private def _from_config_file(path: Path, text: String): LauncherConfig =
    _from_config_file(path, LauncherConfigParser.parse(path, text))

  private def _from_config_file(
    path: Path,
    values: Map[String, Vector[String]]
  ): LauncherConfig =
    _resolve_config_file_directories(fromParsed(values), path)

  private def _resolve_config_file_directories(
    config: LauncherConfig,
    path: Path
  ): LauncherConfig =
    config.copy(
      launcherDevDir = _resolve_config_file_directory(config.launcherDevDir, path),
      runtimeDevDir = _resolve_config_file_directory(config.runtimeDevDir, path),
      developmentLauncherDevDir = _resolve_config_file_directory(config.developmentLauncherDevDir, path),
      developmentRuntimeDevDir = _resolve_config_file_directory(config.developmentRuntimeDevDir, path)
    )

  private def _resolve_config_file_directory(
    value: Option[String],
    path: Path
  ): Option[String] =
    value.map { raw =>
      val configuredpath = Path.of(raw)
      if (configuredpath.isAbsolute)
        raw
      else
        path.toAbsolutePath.normalize.getParent.resolve(configuredpath).normalize.toString
    }

  private def _env_first(environment: Map[String, String], keys: String*): Option[String] =
    keys.toVector.flatMap(k => environment.get(k)).headOption.map(_.trim).filter(_.nonEmpty)

  def render(config: LauncherConfig): String = {
    val c = config.normalizedWithDefaults
    val runtime = c.runtimeVersion.getOrElse("(not configured)")
    val catalog = c.runtimeCatalogUrl.getOrElse("(not configured)")
    val runtimedevdir = c.runtimeDevDir.getOrElse("(not configured)")
    val developmentenabled = c.developmentEnabled.getOrElse(false)
    val developmentlauncherenabled = c.isDevelopmentLauncherEnabled
    val developmentruntimeenabled = c.isDevelopmentRuntimeEnabled
    val developmentlauncherdevdir = c.developmentLauncherDevDir.getOrElse("(not configured)")
    val developmentruntimedevdir = c.developmentRuntimeDevDir.getOrElse("(not configured)")
    val mavens = c.mavenRepositories.mkString(", ")
    val coursiers = c.coursierRepositories.mkString(", ")
    s"""runtime.version: $runtime
       |runtime.catalog.url: $catalog
       |runtime.devDir: $runtimedevdir
       |development.enabled: $developmentenabled
       |development.launcher.enabled: $developmentlauncherenabled
       |development.launcher.devDir: $developmentlauncherdevdir
       |development.runtime.enabled: $developmentruntimeenabled
       |development.runtime.devDir: $developmentruntimedevdir
       |repositories.maven: $mavens
       |repositories.coursier: $coursiers""".stripMargin
  }
}

object LauncherConfigParser {
  def parse(
    path: Path,
    text: String
  ): Map[String, Vector[String]] =
    CoreLauncherConfigParser.parse(path, text)
}
