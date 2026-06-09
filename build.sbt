ThisBuild / organization := "org.simplemodeling"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.7"
ThisBuild / publishMavenStyle := true

lazy val publishCozyCoursierChannel = taskKey[File]("Publish the Cozy launcher Coursier channel descriptor into the Cozy warehouse channel.")

def cozyPublishRepositoryFile(resolver: Resolver): Option[File] =
  resolver match {
    case m: MavenRepository =>
      val root = m.root
      if (root.startsWith("file:"))
        Some(new File(new java.net.URI(root)))
      else
        Some(file(root))
    case f: FileRepository =>
      f.patterns.artifactPatterns.headOption.flatMap { pattern =>
        val marker = "/[organisation]/"
        val index = pattern.indexOf(marker)
        if (index >= 0)
          Some(file(pattern.take(index)))
        else
          None
      }
    case _ =>
      None
  }

def cozyWarehouseDirFromMavenRepository(repository: File): File =
  repository.getCanonicalFile match {
    case canonical
        if canonical.getName == "maven" &&
          canonical.getParentFile != null &&
          canonical.getParentFile.getName == "repository" =>
      canonical.getParentFile.getParentFile
    case canonical if canonical.getName == "maven" =>
      canonical.getParentFile
    case canonical =>
      sys.error(
        s"Cozy Coursier channel publish requires publishTo to point at a Maven repository " +
          s"under a warehouse, but got: ${canonical}"
      )
  }

def cozyCoursierChannelEntryJson(version: String): String =
  s"""{
     |  "repositories": [
     |    "central",
     |    "https://www.simplemodeling.org/repository/maven"
     |  ],
     |  "dependencies": [
     |    "org.simplemodeling:cozy-launcher_3:$version"
     |  ],
     |  "mainClass": "cozy.launcher.CozyLauncherMain"
     |}""".stripMargin

def cozyParseCoursierChannelEntries(text: String): Vector[(String, String)] = {
  val source = text.trim
  if (source.isEmpty)
    Vector.empty
  else {
    val start = source.indexOf('{')
    val end = source.lastIndexOf('}')
    if (start < 0 || end <= start)
      Vector.empty
    else {
      var i = start + 1
      val entries = Vector.newBuilder[(String, String)]
      def skipWhitespaceAndCommas(): Unit =
        while (i < end && (source.charAt(i).isWhitespace || source.charAt(i) == ',')) i += 1
      while (i < end) {
        skipWhitespaceAndCommas()
        if (i < end && source.charAt(i) == '"') {
          val keyStart = i + 1
          val keyEnd = source.indexOf('"', keyStart)
          if (keyEnd < 0) {
            i = end
          } else {
            val key = source.substring(keyStart, keyEnd)
            i = keyEnd + 1
            while (i < end && (source.charAt(i).isWhitespace || source.charAt(i) == ':')) i += 1
            if (i < end && source.charAt(i) == '{') {
              val valueStart = i
              var depth = 0
              var done = false
              while (i < source.length && !done) {
                source.charAt(i) match {
                  case '{' => depth += 1
                  case '}' =>
                    depth -= 1
                    if (depth == 0) done = true
                  case _ => ()
                }
                i += 1
              }
              entries += key -> source.substring(valueStart, i)
            } else {
              i = end
            }
          }
        } else {
          i += 1
        }
      }
      entries.result()
    }
  }
}

def cozyCoursierChannelJson(
  version: String,
  existing: Option[String]
): String = {
  val entries =
    existing.toVector.flatMap(cozyParseCoursierChannelEntries).filterNot(_._1 == "cozy") :+
      ("cozy" -> cozyCoursierChannelEntryJson(version))
  val rendered = entries.map { case (name, value) =>
    val lines = value.linesIterator.toVector
    val head = lines.headOption.getOrElse("{}")
    val tail = lines.drop(1).map(line => s"  $line")
    (s"""  "$name": $head""" +: tail).mkString("\n")
  }
  (Vector("{") ++ rendered.zipWithIndex.map { case (entry, index) =>
    if (index + 1 == rendered.length) entry else s"$entry,"
  } ++ Vector("}")).mkString("\n") + "\n"
}

def cozyPublishCoursierChannelFile(
  version: String,
  publishResolver: Option[Resolver],
  baseDir: File,
  log: sbt.util.Logger
): File = {
  val warehouseDir =
    publishResolver
      .flatMap(cozyPublishRepositoryFile)
      .map(cozyWarehouseDirFromMavenRepository)
      .getOrElse(cozyWarehouseDirFromMavenRepository(baseDir / "maven-local"))
  val target = warehouseDir / "repository" / "cozy" / "coursier-channel.json"
  val existing =
    if (target.isFile)
      Some(IO.read(target))
    else
      None
  IO.createDirectory(target.getParentFile)
  IO.write(target, cozyCoursierChannelJson(version, existing))
  log.info(s"Published Cozy launcher Coursier channel entry to ${target}")
  target
}

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
    publishCozyCoursierChannel := {
      cozyPublishCoursierChannelFile(version.value, publishTo.value, baseDirectory.value, streams.value.log)
    },
    publish / packagedArtifacts := {
      publishCozyCoursierChannel.value
      (publish / packagedArtifacts).value
    }
  )
