package cozy.launcher

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

/*
 * @since   Jun.  9, 2026
 * @version Jun. 27, 2026
 * @author  ASAMI, Tomoharu
 */
final case class LauncherConfig(
  launcherDevDir: Option[String] = None,
  runtimeVersion: Option[String] = None,
  runtimeCatalogUrl: Option[String] = None,
  runtimeDevDir: Option[String] = None,
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

  def withDevelopmentEnabled: LauncherConfig =
    copy(
      launcherDevDir = launcherDevDir.orElse(developmentLauncherDevDir),
      runtimeDevDir = runtimeDevDir.orElse(developmentRuntimeDevDir)
    )

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
    val global = loadFile(paths.globalConfig)
    val project = loadFile(paths.projectConfig)
    val projectlocal = loadFile(paths.projectLocalConfig)
    val base = LauncherConfig()
      .mergeHigher(global)
      .mergeHigher(project)
      .mergeHigher(projectlocal)
    val explicit = configfiles.foldLeft(base) { (acc, file) =>
      acc.mergeHigher(loadRequiredFile(paths.cwd.resolve(file).normalize.toAbsolutePath.normalize))
    }
    val development =
      if (_use_development(environment))
        explicit.withDevelopmentEnabled
      else
        explicit
    development.mergeHigher(fromEnvironment(environment)).normalizedWithDefaults
  }

  def loadFile(path: Path): LauncherConfig =
    if (Files.isRegularFile(path)) {
      val text = Files.readString(path, StandardCharsets.UTF_8)
      fromParsed(LauncherConfigParser.parse(path, text))
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

    LauncherConfig(
      launcherDevDir = _first_("cozy.launcher.dev.dir", "cozy.launcher.dev-dir", "cozy.launcher.devDir", "launcher.dev.dir", "launcher.dev-dir", "launcher.devDir"),
      runtimeVersion = _first_("runtime.version", "cozy.runtime.version", "version"),
      runtimeCatalogUrl = _first_("runtime.catalog.url", "cozy.runtime.catalog.url", "catalog.url"),
      runtimeDevDir = _first_("runtime.dev-dir", "runtime.dev_dir", "runtime.devDir", "runtime.dev.dir", "cozy.runtime.dev-dir", "cozy.runtime.dev_dir", "cozy.runtime.devDir", "cozy.runtime.dev.dir"),
      developmentLauncherDevDir = _first_("development.launcher.dev-dir", "development.launcher.dev_dir", "development.launcher.devDir", "development.launcher.dev.dir", "cozy.development.launcher.dev-dir", "cozy.development.launcher.dev_dir", "cozy.development.launcher.devDir", "cozy.development.launcher.dev.dir"),
      developmentRuntimeDevDir = _first_("development.runtime.dev-dir", "development.runtime.dev_dir", "development.runtime.devDir", "development.runtime.dev.dir", "cozy.development.runtime.dev-dir", "cozy.development.runtime.dev_dir", "cozy.development.runtime.devDir", "cozy.development.runtime.dev.dir"),
      mavenRepositories = _all_("repositories.maven", "cozy.repository.maven"),
      coursierRepositories = _all_("repositories.coursier", "cozy.repository.coursier")
    )
  }

  def fromEnvironment(environment: Map[String, String] = sys.env): LauncherConfig = {
    val usedevelopment = _use_development(environment)
    val runtimedevdir = _env_first(environment, "COZY_RUNTIME_DEV_DIR").orElse {
      if (usedevelopment)
        _env_first(environment, "COZY_PROJECT_DIR")
      else
        None
    }
    LauncherConfig(
      launcherDevDir = _env_first(environment, "COZY_LAUNCHER_DEV_DIR"),
      runtimeVersion = _env_first(environment, "COZY_RUNTIME_VERSION", "COZY_VERSION"),
      runtimeDevDir = runtimedevdir
    )
  }

  private def _use_development(environment: Map[String, String]): Boolean =
    _env_first(environment, "COZY_USE_DEVELOPMENT").exists(_truthy)

  private def _env_first(environment: Map[String, String], keys: String*): Option[String] =
    keys.toVector.flatMap(k => environment.get(k)).headOption.map(_.trim).filter(_.nonEmpty)

  private def _truthy(value: String): Boolean =
    value.trim.toLowerCase match {
      case "true" | "yes" | "on" | "1" => true
      case _ => false
    }

  def render(config: LauncherConfig): String = {
    val c = config.normalizedWithDefaults
    val runtime = c.runtimeVersion.getOrElse("(not configured)")
    val catalog = c.runtimeCatalogUrl.getOrElse("(not configured)")
    val runtimedevdir = c.runtimeDevDir.getOrElse("(not configured)")
    val developmentlauncherdevdir = c.developmentLauncherDevDir.getOrElse("(not configured)")
    val developmentruntimedevdir = c.developmentRuntimeDevDir.getOrElse("(not configured)")
    val mavens = c.mavenRepositories.mkString(", ")
    val coursiers = c.coursierRepositories.mkString(", ")
    s"""runtime.version: $runtime
       |runtime.catalog.url: $catalog
       |runtime.devDir: $runtimedevdir
       |development.launcher.devDir: $developmentlauncherdevdir
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
    _file_type(path) match {
      case "yaml" | "yml" => _parse_light_yaml(text)
      case "properties" | "props" | "conf" => _parse_properties(text)
      case other =>
        throw CozyException(s"unsupported launcher config file type: .$other; use yaml, yml, properties, props, or conf")
    }

  private def _file_type(path: Path): String = {
    val name = path.getFileName.toString
    val i = name.lastIndexOf('.')
    if (i >= 0 && i + 1 < name.length)
      name.substring(i + 1).toLowerCase
    else
      "yaml"
  }

  private def _parse_properties(text: String): Map[String, Vector[String]] = {
    var values = Map.empty[String, Vector[String]]
    text.linesIterator.foreach { raw =>
      val uncommented = _strip_comment(raw)
      val trimmed = uncommented.trim
      if (trimmed.nonEmpty) {
        val idx = _key_value_index(trimmed)
        if (idx >= 0) {
          val key = trimmed.substring(0, idx).trim
          val value = trimmed.substring(idx + 1).trim
          _put_value(key, value, values).foreach(v => values = v)
        }
      }
    }
    values
  }

  private def _parse_light_yaml(text: String): Map[String, Vector[String]] = {
    var values = Map.empty[String, Vector[String]]
    var stack = Vector.empty[(Int, String)]
    var pendingkey: Option[String] = None

    def put(path: String, value: String): Unit =
      _put_value(path, value, values).foreach(v => values = v)

    text.linesIterator.foreach { raw =>
      val uncommented = _strip_comment(raw)
      if (uncommented.trim.nonEmpty) {
        val indent = uncommented.takeWhile(_ == ' ').length
        val trimmed = uncommented.trim
        stack = stack.dropRight(stack.count(_._1 >= indent))
        if (trimmed.startsWith("- ")) {
          pendingkey.foreach(k => put(k, trimmed.drop(2)))
        } else {
          val idx = _key_value_index(trimmed)
          if (idx >= 0) {
            val key = trimmed.substring(0, idx).trim
            val value = trimmed.substring(idx + 1).trim
            val path = (stack.map(_._2) :+ key).mkString(".")
            if (value.isEmpty) {
              stack = stack :+ (indent, key)
              pendingkey = Some(path)
            } else {
              put(path, value)
              pendingkey = Some(path)
            }
          }
        }
      }
    }
    values
  }

  private def _put_value(
    rawkey: String,
    rawvalue: String,
    values: Map[String, Vector[String]]
  ): Option[Map[String, Vector[String]]] = {
    val key = rawkey.trim
    val value = _unquote(rawvalue.trim)
    if (key.isEmpty || value.isEmpty)
      None
    else
      Some(values.updated(key, values.getOrElse(key, Vector.empty) :+ value))
  }

  private def _strip_comment(line: String): String = {
    val index = line.indexOf('#')
    if (index >= 0) line.take(index) else line
  }

  private def _key_value_index(line: String): Int = {
    val colon = line.indexOf(':')
    val equals = line.indexOf('=')
    if (colon < 0) equals
    else if (equals < 0) colon
    else math.min(colon, equals)
  }

  private def _unquote(value: String): String =
    if (value.length >= 2 && ((value.head == '"' && value.last == '"') || (value.head == '\'' && value.last == '\'')))
      value.substring(1, value.length - 1)
    else
      value
}
