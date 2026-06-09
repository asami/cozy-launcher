package cozy.launcher

import java.nio.file.{Path, Paths}

/*
 * @since   Jun.  9, 2026
 * @version Jun. 10, 2026
 * @author  ASAMI, Tomoharu
 */
final case class LauncherPaths(
  home: Path = Paths.get(sys.props.getOrElse("user.home", ".")).toAbsolutePath.normalize,
  cwd: Path = Paths.get("").toAbsolutePath.normalize
) {
  val cozyHome: Path = home.resolve(".cozy")
  val globalConfig: Path = cozyHome.resolve("launcher.yaml")
  val projectConfig: Path = cwd.resolve("conf").resolve("cozy").resolve("launcher.yaml")
  val projectLocalConfig: Path = cwd.resolve(".cozy").resolve("launcher.yaml")
  val globalVersion: Path = cozyHome.resolve("version")
  val projectVersion: Path = cwd.resolve(".cozy").resolve("version")
  val runtimeCatalog: Path = cozyHome.resolve("catalog").resolve("cozy").resolve("runtime-catalog.yaml")
  val runtimeRoot: Path = cozyHome.resolve("runtimes")
  val coursierCache: Path = cozyHome.resolve("cache").resolve("coursier")

  def withCwd(path: Path): LauncherPaths =
    copy(cwd = path.toAbsolutePath.normalize)
}
