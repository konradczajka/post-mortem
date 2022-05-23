val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "post-mortem",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.7.0",
      "org.cosplayengine" % "cosplay" % "0.6.6",
      "dev.optics" %% "monocle-core"  % "3.1.0",
      "dev.optics" %% "monocle-macro" % "3.1.0",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
