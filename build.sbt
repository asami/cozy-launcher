import org.goldenport.cozy.CozyPlugin.autoImport._

ThisBuild / organization := "org.simplemodeling"
ThisBuild / version := "0.1.5-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.7"
ThisBuild / publishMavenStyle := true

resolvers += "SimpleModeling.org" at "https://www.simplemodeling.org/repository/maven"

libraryDependencies ++= Seq(
  "org.goldenport" %% "goldenport-launcher-core" % "0.1.0",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)

cozyCoursierChannelPath := "repository/cozy/coursier-channel.json"

def ensurePublishAllowed(version: String): Unit = {
  if (version.endsWith("-SNAPSHOT"))
    sys.error(s"Refusing to publish SNAPSHOT cozy-launcher version $version. " +
      "Use publishLocal for development versions.")
}

def ensurePublishLocalAllowed(version: String): Unit = {
  if (!version.endsWith("-SNAPSHOT"))
    sys.error(s"Refusing to publishLocal release cozy-launcher version $version. " +
      "Use publish for public release versions.")
}

publish / skip := {
  ensurePublishAllowed(version.value)
  false
}

publishLocal / skip := {
  ensurePublishLocalAllowed(version.value)
  false
}

cozyCoursierChannelEntries := Seq(CozyCoursierChannelEntry(
  name = "cozy",
  repositories = Seq("central", "https://www.simplemodeling.org/repository/maven"),
  dependencies = Seq(s"org.simplemodeling:cozy-launcher_3:${version.value}"),
  mainClass = "cozy.launcher.CozyLauncherMain"
))

def launcherBuildInfoSource(target: File, packagename: String, launchername: String, launcherversion: String): File = {
  val file = target / "LauncherBuildInfo.scala"
  IO.write(file,
    s"""package $packagename
       |
       |object LauncherBuildInfo {
       |  val name: String = "$launchername"
       |  val version: String = "$launcherversion"
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
