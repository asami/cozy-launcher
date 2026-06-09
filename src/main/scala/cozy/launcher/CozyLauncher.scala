package cozy.launcher

import java.nio.charset.StandardCharsets
import java.nio.file.Files

/*
 * @since   Jun.  9, 2026
 * @version Jun. 10, 2026
 * @author  ASAMI, Tomoharu
 */
final class CozyLauncher(
  paths: LauncherPaths = LauncherPaths(),
  runtimeresolver: CozyRuntimeResolver = CoursierCozyRuntimeResolver(),
  cozyinvoker: CozyInvoker = CozyInvoker(),
  launcherdevinvoker: LauncherDevInvoker = LauncherDevInvoker.System,
  runtimeDevInvoker: CozyRuntimeDevInvoker = CozyRuntimeDevInvoker.System
) {
  def run(args: Vector[String]): Int = {
    val effectiveargs = _args_from_file().getOrElse(args)
    val (configfiles, commandargs) = _take_config_options(effectiveargs)
    val config = LauncherConfig.load(paths, configfiles)
    _delegate_launcher_dev_dir(config, effectiveargs) match {
      case Some(code) => return code
      case None => ()
    }
    val command = CozyCommandParser.parse(commandargs)
    command match {
      case CozyCommand.Version =>
        println(s"${LauncherBuildInfo.name} ${LauncherBuildInfo.version}")
        0
      case CozyCommand.Help =>
        println(CozyCommandParser.helpText)
        0
      case runtime: CozyCommand.Runtime =>
        _run_runtime(runtime, config)
      case execute: CozyCommand.Execute =>
        _run_execute(execute, config)
    }
  }

  private def _delegate_launcher_dev_dir(
    config: LauncherConfig,
    args: Vector[String]
  ): Option[Int] =
    if (sys.env.get("COZY_LAUNCHER_DEV_DELEGATED").contains("1"))
      None
    else
      config.launcherDevDir.map { dir =>
        val path = paths.cwd.resolve(dir).normalize.toAbsolutePath.normalize
        launcherdevinvoker.invoke(path, args)
      }

  private def _run_runtime(
    command: CozyCommand.Runtime,
    config: LauncherConfig
  ): Int = {
    val store = RuntimeVersionStore(paths)
    val catalogstore = RuntimeCatalogStore(paths)
    command match {
      case CozyCommand.Runtime.Current =>
        _run_runtime_current(store, catalogstore, config)
      case CozyCommand.Runtime.RemoteList =>
        val catalog = catalogstore.loadOrRefresh(config)
          .getOrElse(throw CozyException("failed to load Cozy runtime catalog"))
        println(catalog.renderRemoteList)
        0
      case CozyCommand.Runtime.CatalogRefresh =>
        catalogstore.refresh(config)
        println(s"refreshed Cozy runtime catalog: ${paths.runtimeCatalog}")
        0
      case CozyCommand.Runtime.CatalogShow =>
        val catalog = catalogstore.loadOrRefresh(config)
          .getOrElse(throw CozyException("failed to load Cozy runtime catalog"))
        println(catalog.render)
        0
      case CozyCommand.Runtime.Channels =>
        val catalog = catalogstore.loadOrRefresh(config)
          .getOrElse(throw CozyException("failed to load Cozy runtime catalog"))
        println(catalog.renderChannels)
        0
      case CozyCommand.Runtime.LocalList =>
        val installed =
          if (Files.isDirectory(paths.runtimeRoot)) {
            val stream = Files.list(paths.runtimeRoot)
            try {
              import scala.jdk.CollectionConverters.*
              stream.iterator().asScala.filter(Files.isDirectory(_)).map(_.getFileName.toString).toVector.sorted
            } finally {
              stream.close()
            }
          } else {
            Vector.empty
          }
        installed.foreach(println)
        0
      case CozyCommand.Runtime.Install(version) =>
        val concreteversion = runtimeresolver.resolveVersion(version, config, paths)
        runtimeresolver.resolve(concreteversion, config, paths)
        println(s"installed Cozy runtime $concreteversion")
        0
      case CozyCommand.Runtime.Use(version, target) =>
        val concreteversion = runtimeresolver.resolveVersion(version, config, paths)
        val resolvedtarget = _resolve_runtime_use_target(target)
        resolvedtarget match {
          case CozyCommand.RuntimeUseTarget.Global => store.useGlobal(version)
          case CozyCommand.RuntimeUseTarget.Project => store.useProject(version)
          case CozyCommand.RuntimeUseTarget.Auto => throw CozyException("unresolved runtime use target")
        }
        println(s"using Cozy runtime $version -> $concreteversion (${resolvedtarget.toString.toLowerCase})")
        0
      case CozyCommand.Runtime.CacheStatus() =>
        println(s"cozy home: ${paths.cozyHome}")
        println(s"runtime cache: ${paths.runtimeRoot}")
        println(s"coursier cache: ${paths.coursierCache}")
        0
      case CozyCommand.Runtime.ConfigShow() =>
        println(LauncherConfig.render(config))
        0
    }
  }

  private def _run_runtime_current(
    store: RuntimeVersionStore,
    catalogstore: RuntimeCatalogStore,
    config: LauncherConfig
  ): Int = {
    val selector = store.current(None, config)
    val current = runtimeresolver.resolveVersion(selector, config, paths)
    println(current)
    _warn_if_runtime_catalog_is_stale(selector, current, catalogstore, config)
    0
  }

  private def _warn_if_runtime_catalog_is_stale(
    selector: String,
    current: String,
    catalogstore: RuntimeCatalogStore,
    config: LauncherConfig
  ): Unit =
    if (_is_dynamic_runtime_selector(selector)) {
      val remoteversion =
        try Some(catalogstore.fetch(config).resolve(selector).version)
        catch {
          case _: Throwable => None
        }
      remoteversion.filter(_ != current).foreach { version =>
        Console.err.println(
          s"warning: cached Cozy runtime catalog resolves $selector to $current, but remote catalog resolves it to $version."
        )
        Console.err.println("Run 'cozy runtime refresh' to update the local runtime catalog cache.")
      }
    }

  private def _is_dynamic_runtime_selector(selector: String): Boolean =
    selector match {
      case "recommended" | "latest" | "latest-stable" | "latest.release" | "latest-snapshot" | "newest" => true
      case _ => false
    }

  private def _run_execute(
    command: CozyCommand.Execute,
    config: LauncherConfig
  ): Int = {
    command.runtimeDevDir.orElse(config.runtimeDevDir) match {
      case Some(dir) =>
        val path = paths.cwd.resolve(dir).normalize.toAbsolutePath.normalize
        runtimeDevInvoker.invoke(path, command.args)
      case None =>
        val store = RuntimeVersionStore(paths)
        val runtimeversion = store.current(command.runtimeVersion, config)
        val classpath = runtimeresolver.resolve(runtimeversion, config, paths)
        cozyinvoker.invoke(classpath, command.args)
    }
  }

  private def _resolve_runtime_use_target(
    target: CozyCommand.RuntimeUseTarget
  ): CozyCommand.RuntimeUseTarget =
    target match {
      case CozyCommand.RuntimeUseTarget.Auto =>
        if (Files.exists(paths.cwd.resolve(".cozy")))
          CozyCommand.RuntimeUseTarget.Project
        else
          CozyCommand.RuntimeUseTarget.Global
      case x => x
    }

  private def _take_config_options(args: Vector[String]): (Vector[String], Vector[String]) = {
    val configfiles = Vector.newBuilder[String]
    val commandargs = Vector.newBuilder[String]
    var i = 0
    var passthrough = false
    while (i < args.length) {
      if (passthrough) {
        commandargs += args(i)
        i += 1
      } else {
        args(i) match {
          case "--" =>
            passthrough = true
            commandargs += args(i)
            i += 1
          case "--config" | "--launcher-config" =>
            if (i + 1 >= args.length)
              throw CozyException(s"${args(i)} requires a file")
            configfiles += args(i + 1)
            i += 2
          case x if x.startsWith("--config=") =>
            configfiles += x.stripPrefix("--config=")
            i += 1
          case x if x.startsWith("--launcher-config=") =>
            configfiles += x.stripPrefix("--launcher-config=")
            i += 1
          case x =>
            commandargs += x
            i += 1
        }
      }
    }
    (configfiles.result(), commandargs.result())
  }

  private def _args_from_file(): Option[Vector[String]] =
    sys.env.get("COZY_LAUNCHER_ARGS_FILE").map { file =>
      val text = Files.readString(java.nio.file.Path.of(file), StandardCharsets.UTF_8)
      if (text.isEmpty)
        Vector.empty
      else
        text.split("\u0000", -1).toVector.filter(_.nonEmpty)
    }
}

object CozyLauncher {
  def apply(): CozyLauncher =
    new CozyLauncher()
}
