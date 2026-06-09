package cozy.launcher

/*
 * @since   Jun.  9, 2026
 * @version Jun.  9, 2026
 * @author  ASAMI, Tomoharu
 */
object CozyLauncherMain {
  def main(args: Array[String]): Unit = {
    val code =
      try CozyLauncher().run(args.toVector)
      catch {
        case e: CozyException =>
          Console.err.println(e.getMessage)
          e.code
        case e: Throwable =>
          Console.err.println(e.getMessage)
          1
      }
    if (code != 0)
      sys.exit(code)
  }
}
