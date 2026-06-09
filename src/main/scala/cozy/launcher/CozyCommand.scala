package cozy.launcher

/*
 * @since   Jun.  9, 2026
 * @version Jun. 10, 2026
 * @author  ASAMI, Tomoharu
 */
sealed trait CozyCommand

object CozyCommand {
  final case class Execute(
    args: Vector[String],
    runtimeVersion: Option[String],
    runtimeDevDir: Option[String]
  ) extends CozyCommand

  sealed trait Runtime extends CozyCommand
  object Runtime {
    case object Current extends Runtime
    case object RemoteList extends Runtime
    case object CatalogRefresh extends Runtime
    case object CatalogShow extends Runtime
    case object Channels extends Runtime
    case object LocalList extends Runtime
    final case class Install(version: String) extends Runtime
    final case class Use(version: String, target: RuntimeUseTarget) extends Runtime
    final case class CacheStatus() extends Runtime
    final case class ConfigShow() extends Runtime
  }

  enum RuntimeUseTarget {
    case Auto, Global, Project
  }

  case object Version extends CozyCommand
  case object Help extends CozyCommand
}

object CozyCommandParser {
  def parse(args: Vector[String]): CozyCommand = {
    val (runtimeversion, runtimedevdir, rest) = _take_global_runtime(args)
    rest match {
      case Vector("--version") | Vector("version") | Vector("launcher", "version") | Vector("launcher", "--version") =>
        CozyCommand.Version
      case Vector() | Vector("-h") | Vector("--help") | Vector("help") | Vector("launcher", "help") =>
        CozyCommand.Help
      case Vector("runtime", tail*) =>
        _parse_runtime(tail.toVector)
      case other =>
        CozyCommand.Execute(other, runtimeversion, runtimedevdir)
    }
  }

  private def _take_global_runtime(args: Vector[String]): (Option[String], Option[String], Vector[String]) = {
    val out = Vector.newBuilder[String]
    var runtime: Option[String] = None
    var runtimedevdir: Option[String] = None
    var passthrough = false
    var i = 0
    while (i < args.length) {
      if (passthrough) {
        out += args(i)
        i += 1
      } else {
        args(i) match {
          case "--" =>
            passthrough = true
            out += args(i)
            i += 1
          case "--runtime" =>
            if (i + 1 >= args.length)
              throw CozyException("--runtime requires a value")
            runtime = Some(args(i + 1))
            i += 2
          case x if x.startsWith("--runtime=") =>
            runtime = Some(x.stripPrefix("--runtime="))
            i += 1
          case "--runtime-dev-dir" =>
            if (i + 1 >= args.length)
              throw CozyException("--runtime-dev-dir requires a value")
            runtimedevdir = Some(args(i + 1))
            i += 2
          case x if x.startsWith("--runtime-dev-dir=") =>
            runtimedevdir = Some(x.stripPrefix("--runtime-dev-dir="))
            i += 1
          case x =>
            out += x
            i += 1
        }
      }
    }
    (runtime, runtimedevdir, out.result())
  }

  private def _parse_runtime(args: Vector[String]): CozyCommand.Runtime =
    args match {
      case Vector("current") => CozyCommand.Runtime.Current
      case Vector("remote", "list") => CozyCommand.Runtime.RemoteList
      case Vector("refresh") => CozyCommand.Runtime.CatalogRefresh
      case Vector("catalog", "show") => CozyCommand.Runtime.CatalogShow
      case Vector("channels") => CozyCommand.Runtime.Channels
      case Vector("list") => CozyCommand.Runtime.LocalList
      case Vector("local", "list") => CozyCommand.Runtime.LocalList
      case Vector("install", version) => CozyCommand.Runtime.Install(version)
      case Vector("use", version) =>
        CozyCommand.Runtime.Use(version, CozyCommand.RuntimeUseTarget.Auto)
      case Vector("use", version, "--global") =>
        CozyCommand.Runtime.Use(version, CozyCommand.RuntimeUseTarget.Global)
      case Vector("use", version, "--project") =>
        CozyCommand.Runtime.Use(version, CozyCommand.RuntimeUseTarget.Project)
      case Vector("cache", "status") => CozyCommand.Runtime.CacheStatus()
      case Vector("config", "show") => CozyCommand.Runtime.ConfigShow()
      case other =>
        throw CozyException(s"unknown cozy runtime command: ${other.mkString(" ")}")
    }

  val helpText: String =
    """Usage:
      |  cozy [--runtime <version>] <cozy-args...>
      |  cozy --version
      |  cozy launcher version
      |  cozy launcher help
      |  cozy runtime current
      |  cozy runtime refresh
      |  cozy runtime remote list
      |  cozy runtime catalog show
      |  cozy runtime channels
      |  cozy runtime list
      |  cozy runtime local list
      |  cozy runtime install <version>
      |  cozy runtime use <version>
      |  cozy runtime use <version> --global
      |  cozy runtime use <version> --project
      |  cozy runtime cache status
      |  cozy runtime config show
      |
      |Runtime:
      |  The launcher selects a Cozy runtime from the runtime catalog, resolves it
      |  with Coursier, and invokes cozy.Cozy in the same JVM.
      |  --runtime-dev-dir <dir> runs cozy.Cozy from a local Cozy checkout with sbt.
      |  Version selectors: latest, latest-stable, latest-snapshot, newest, recommended.
      |  Launcher config loads from ~/.cozy/launcher.yaml, conf/cozy/launcher.yaml, then .cozy/launcher.yaml.
      |  .cozy/config.yaml remains build/publish configuration and is not launcher configuration.
      |""".stripMargin
}
