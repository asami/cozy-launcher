import org.goldenport.cozy.CozyPlugin.autoImport._

ThisBuild / organization := "org.simplemodeling"
ThisBuild / version := "0.1.2"
ThisBuild / scalaVersion := "3.3.7"
ThisBuild / publishMavenStyle := true

cozyCoursierChannelPath := "repository/cozy/coursier-channel.json"

cozyCoursierChannelEntries := Seq(CozyCoursierChannelEntry(
  name = "cozy",
  repositories = Seq("central", "https://www.simplemodeling.org/repository/maven"),
  dependencies = Seq(s"org.simplemodeling:cozy-launcher_3:${version.value}"),
  mainClass = "cozy.launcher.CozyLauncherMain"
))

def launcherBuildInfoSource(target: File, packageName: String, launcherName: String, launcherVersion: String): File = {
  val file = target / "LauncherBuildInfo.scala"
  IO.write(file,
    s"""package $packageName
       |
       |object LauncherBuildInfo {
       |  val name: String = "$launcherName"
       |  val version: String = "$launcherVersion"
       |}
       |""".stripMargin)
  file
}

lazy val root = (project in file("."))
  .enablePlugins(org.goldenport.cozy.CozyPlugin)
  .settings(
    name := "cozy-launcher",
    Compile / sourceGenerators += Def.task {
      Seq(launcherBuildInfoSource(
        (Compile / sourceManaged).value / "launcher-build-info",
        "cozy.launcher",
        name.value,
        version.value
      ))
    }.taskValue,
    Compile / mainClass := Some("cozy.launcher.CozyLauncherMain"),
    Test / test := {
      (Test / runMain).toTask(" cozy.launcher.CozyLauncherSpec").value
    },
    publishTo := {
      val repo = sys.env.get("SIMPLEMODELING_MAVEN_LOCAL")
        .map(file)
        .getOrElse(baseDirectory.value / "maven-local")
      Some(Resolver.file("local-simplemodeling-maven", repo))
    },
    Compile / packageDoc / publishArtifact := false,
    publish / packagedArtifacts := {
      cozyPublishCoursierChannel.value
      (publish / packagedArtifacts).value
    }
  )
