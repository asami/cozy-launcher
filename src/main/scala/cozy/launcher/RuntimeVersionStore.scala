package cozy.launcher

import java.nio.charset.StandardCharsets
import java.nio.file.Files

/*
 * @since   Jun.  9, 2026
 * @version Jun.  9, 2026
 * @author  ASAMI, Tomoharu
 */
final class RuntimeVersionStore(paths: LauncherPaths) {
  def current(cliversion: Option[String], config: LauncherConfig): String =
    cliversion
      .orElse(config.runtimeVersion)
      .orElse(_read(paths.projectVersion))
      .orElse(_read(paths.globalVersion))
      .getOrElse(LauncherConfig.DEFAULT_RUNTIME_VERSION)

  def useGlobal(version: String): Unit =
    _write(paths.globalVersion, version)

  def useProject(version: String): Unit =
    _write(paths.projectVersion, version)

  private def _read(path: java.nio.file.Path): Option[String] =
    if (Files.isRegularFile(path))
      Some(Files.readString(path, StandardCharsets.UTF_8).trim).filter(_.nonEmpty)
    else
      None

  private def _write(path: java.nio.file.Path, version: String): Unit = {
    Files.createDirectories(path.getParent)
    Files.writeString(path, version.trim + "\n", StandardCharsets.UTF_8)
  }
}
