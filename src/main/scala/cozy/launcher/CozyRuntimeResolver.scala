package cozy.launcher

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import scala.sys.process.*

/*
 * @since   Jun.  9, 2026
 * @version Jun. 20, 2026
 * @author  ASAMI, Tomoharu
 */
trait CozyRuntimeResolver {
  def resolveVersion(version: String, config: LauncherConfig, paths: LauncherPaths): String =
    version

  def resolve(version: String, config: LauncherConfig, paths: LauncherPaths): Vector[Path]
}

final class CoursierCozyRuntimeResolver(
  coursiercommand: String = sys.env.getOrElse("COZY_COURSIER_COMMAND", "cs")
) extends CozyRuntimeResolver {
  override def resolveVersion(version: String, config: LauncherConfig, paths: LauncherPaths): String =
    _catalog_version(version, config, paths) match {
      case Some(v) =>
        v.warnIfDeprecated()
        v.version
      case None =>
        _fallback_version(version, config)
    }

  def resolve(version: String, config: LauncherConfig, paths: LauncherPaths): Vector[Path] = {
    val catalog = RuntimeCatalogStore(paths).loadOrRefresh(config)
    val catalogversion = _catalog_version(version, config, paths, catalog)
    val concreteversion =
      catalogversion match {
        case Some(v) =>
          v.warnIfDeprecated()
          v.version
        case None =>
          _fallback_version(version, config)
      }
    val metadata = paths.runtimeRoot.resolve(concreteversion).resolve("classpath.txt")
    if (Files.isRegularFile(metadata)) {
      _read_classpath(metadata)
    } else {
      Files.createDirectories(metadata.getParent)
      Files.createDirectories(paths.coursierCache)
      val effectiveconfig = catalog.map(config.withCatalog).getOrElse(config)
      val repositories = (effectiveconfig.coursierRepositories ++ effectiveconfig.mavenRepositories).distinct.flatMap(r => Vector("-r", r))
      val module = catalogversion.map(_.moduleCoordinate).getOrElse(s"org.simplemodeling:cozy_2.12:$concreteversion")
      val command =
        Vector(coursiercommand, "fetch", "--classpath", "--cache", paths.coursierCache.toString) ++
          repositories ++
          Vector(module)
      val out = new StringBuilder
      val err = new StringBuilder
      val code = Process(command).!(ProcessLogger(out append _, err append _))
      if (code != 0) {
        throw CozyException(
          s"failed to resolve Cozy runtime $concreteversion with Coursier: ${err.toString.trim}",
          2
        )
      }
      val classpath = out.toString.trim
      if (classpath.isEmpty)
        throw CozyException(s"Coursier returned an empty classpath for Cozy runtime $concreteversion", 2)
      Files.writeString(metadata, classpath + "\n", StandardCharsets.UTF_8)
      _classpath_to_paths(classpath)
    }
  }

  private def _catalog_version(
    version: String,
    config: LauncherConfig,
    paths: LauncherPaths
  ): Option[RuntimeCatalogVersion] =
    _catalog_version(version, config, paths, RuntimeCatalogStore(paths).loadOrRefresh(config))

  private def _catalog_version(
    version: String,
    config: LauncherConfig,
    paths: LauncherPaths,
    catalog: Option[RuntimeCatalog]
  ): Option[RuntimeCatalogVersion] =
    catalog.map(_.resolve(version))

  private def _fallback_version(version: String, config: LauncherConfig): String =
    version match {
      case "latest" | "latest-stable" | "latest.release" =>
        _latest_release(config)
      case "latest-snapshot" =>
        _latest_snapshot(config)
      case "newest" =>
        _newest(config)
      case "recommended" =>
        throw CozyException("failed to resolve recommended Cozy runtime version from runtime catalog")
      case x =>
        x
    }

  private def _newest(config: LauncherConfig): String =
    config.mavenRepositories.iterator.flatMap(_metadata_versions).nextOption()
      .getOrElse(throw CozyException("failed to resolve newest Cozy runtime version from Maven repositories"))

  private def _latest_release(config: LauncherConfig): String =
    config.mavenRepositories.iterator.flatMap(_metadata_versions).find(!_.endsWith("-SNAPSHOT"))
      .getOrElse(throw CozyException("failed to resolve latest Cozy runtime version from Maven repositories"))

  private def _latest_snapshot(config: LauncherConfig): String =
    config.mavenRepositories.iterator.flatMap(_metadata_versions).find(_.endsWith("-SNAPSHOT"))
      .getOrElse(throw CozyException("failed to resolve latest snapshot Cozy runtime version from Maven repositories"))

  private def _metadata_versions(repository: String): Vector[String] = {
    val url = _join(repository, "org", "simplemodeling", "cozy_2.12", "maven-metadata.xml")
    try {
      val connection = java.net.URI.create(url).toURL.openConnection()
      connection.setConnectTimeout(2000)
      connection.setReadTimeout(5000)
      val text = scala.util.Using.resource(scala.io.Source.fromInputStream(connection.getInputStream, "UTF-8"))(_.mkString)
      val latest = _first_tag(text, "latest").orElse(_first_tag(text, "release")).toVector
      val versions = "<version>([^<]+)</version>".r.findAllMatchIn(text).map(_.group(1).trim).filter(_.nonEmpty).toVector.reverse
      (latest ++ versions).distinct
    } catch {
      case _: Throwable => Vector.empty
    }
  }

  private def _read_classpath(path: Path): Vector[Path] =
    _classpath_to_paths(Files.readString(path, StandardCharsets.UTF_8).trim)

  private def _classpath_to_paths(value: String): Vector[Path] =
    value.split(File.pathSeparator).toVector.map(_.trim).filter(_.nonEmpty).map(Path.of(_))

  private def _join(parts: String*): String =
    parts.toVector.zipWithIndex.map { case (p, idx) =>
      if (idx == 0) p.reverse.dropWhile(_ == '/').reverse
      else p.dropWhile(_ == '/').reverse.dropWhile(_ == '/').reverse
    }.mkString("/")

  private def _first_tag(text: String, tag: String): Option[String] =
    s"<$tag>([^<]+)</$tag>".r.findFirstMatchIn(text).map(_.group(1).trim).filter(_.nonEmpty)
}

class CozyInvoker {
  def invoke(classpath: Vector[Path], args: Vector[String]): Int = {
    val urls = classpath.map(_.toUri.toURL).toArray
    val parent = ClassLoader.getPlatformClassLoader
    val loader = new java.net.URLClassLoader(urls, parent)
    val old = Thread.currentThread().getContextClassLoader
    val oldclasspath = sys.props.get("java.class.path")
    try {
      Thread.currentThread().setContextClassLoader(loader)
      sys.props.update("java.class.path", classpath.map(_.toString).mkString(java.io.File.pathSeparator))
      val mainclass = Class.forName("cozy.Cozy", true, loader)
      val main = mainclass.getMethod("main", classOf[Array[String]])
      main.invoke(null, args.toArray)
      0
    } catch {
      case e: java.lang.reflect.InvocationTargetException =>
        throw Option(e.getCause).getOrElse(e)
    } finally {
      oldclasspath match {
        case Some(value) => sys.props.update("java.class.path", value)
        case None => sys.props.remove("java.class.path")
      }
      Thread.currentThread().setContextClassLoader(old)
      loader.close()
    }
  }
}

object CozyInvoker {
  def apply(): CozyInvoker =
    new CozyInvoker()
}

trait RuntimeClasspathExporter {
  def exportRuntimeClasspath(project: Path): String
}

object SbtRuntimeClasspathExporter extends RuntimeClasspathExporter {
  private val _command = Vector(
    "sbt",
    "--batch",
    "-Dsbt.server.autostart=false",
    "-Dsbt.supershell=false",
    "export Runtime / fullClasspath"
  )

  def exportRuntimeClasspath(project: Path): String = {
    val out = new StringBuilder
    val err = new StringBuilder
    val code = Process(_command, project.toFile).
      !(ProcessLogger(line => out.append(line).append("\n"), line => err.append(line).append("\n")))
    if (code != 0)
      throw CozyException(s"failed to resolve Runtime / fullClasspath for ${project}: ${err.toString.trim}", 2)
    out.toString.linesIterator.
      map(_.trim).
      find(line => line.startsWith("/") && line.contains(File.pathSeparator)).
      orElse(out.toString.linesIterator.map(_.trim).find(_.startsWith("/"))).
      getOrElse(throw CozyException(s"failed to find classpath in sbt output for ${project}", 2))
  }
}

private object DevelopmentClasspath {
  def classpathFile(project: Path): Path =
    project.resolve("target").resolve("cozy.d").resolve("runtime-classpath.txt")

  def classpath(project: Path, exporter: RuntimeClasspathExporter): Vector[Path] = {
    val file = classpathFile(project)
    val text =
      if (Files.isRegularFile(file) && Files.size(file) > 0L)
        Files.readString(file, StandardCharsets.UTF_8).trim
      else {
        val exported = exporter.exportRuntimeClasspath(project)
        Files.createDirectories(file.getParent)
        Files.writeString(file, exported + "\n", StandardCharsets.UTF_8)
        exported
      }
    val entries = _classpath_to_paths(text)
    if (entries.isEmpty)
      throw CozyException(s"Runtime / fullClasspath was empty for ${project}", 2)
    entries
  }

  def classpathString(project: Path, exporter: RuntimeClasspathExporter): String =
    classpath(project, exporter).map(_.toString).mkString(File.pathSeparator)

  private def _classpath_to_paths(value: String): Vector[Path] =
    value.split(File.pathSeparator).toVector.map(_.trim).filter(_.nonEmpty).map(Path.of(_))
}

trait LauncherDevInvoker {
  def invoke(devdir: Path, args: Vector[String]): Int
}

object LauncherDevInvoker {
  object System extends LauncherDevInvoker {
    def invoke(devdir: Path, args: Vector[String]): Int = {
      if (!Files.isDirectory(devdir) || !Files.isRegularFile(devdir.resolve("build.sbt")))
        throw CozyException(s"cozy launcher development directory not found: ${devdir}")
      val classpath = DevelopmentClasspath.classpathString(devdir, SbtRuntimeClasspathExporter)
      val argsfile = _write_args_file(args)
      try {
        val builder = new java.lang.ProcessBuilder(
          "java",
          "-cp",
          classpath,
          "cozy.launcher.CozyLauncherMain"
        )
        builder.directory(Path.of(sys.props("user.dir")).toAbsolutePath.normalize.toFile)
        builder.inheritIO()
        builder.environment().put("COZY_LAUNCHER_DEV_DELEGATED", "1")
        builder.environment().put("COZY_LAUNCHER_ARGS_FILE", argsfile.toString)
        builder.start().waitFor()
      } finally {
        Files.deleteIfExists(argsfile)
      }
    }

    private def _write_args_file(args: Vector[String]): Path = {
      val argsfile = Files.createTempFile("cozy-launcher-args-", ".bin")
      val payload =
        if (args.isEmpty)
          ""
        else
          args.mkString("", "\u0000", "\u0000")
      Files.write(argsfile, payload.getBytes(StandardCharsets.UTF_8))
      argsfile
    }
  }
}

trait CozyRuntimeDevInvoker {
  def invoke(devdir: Path, args: Vector[String]): Int
}

object CozyRuntimeDevInvoker {
  object System extends CozyRuntimeDevInvoker {
    private val _invoker = CozyInvoker()

    def invoke(devdir: Path, args: Vector[String]): Int = {
      if (!Files.isDirectory(devdir) || !Files.isRegularFile(devdir.resolve("build.sbt")))
        throw CozyException(s"cozy runtime development directory not found: ${devdir}")
      _invoker.invoke(DevelopmentClasspath.classpath(devdir, SbtRuntimeClasspathExporter), args)
    }
  }
}
