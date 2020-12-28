
ThisBuild / scalaVersion     := "2.12.12"
ThisBuild / version          := "0.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "presence-bits-compression",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % "3.4.+",
      "edu.berkeley.cs" %% "chisel-iotesters" % "1.5.+",
      "io.j-marjanovic" %% "chisel-bfmtester" % "0.4.0-SNAPSHOT"
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit"
    ),
    //addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.4.1" cross CrossVersion.full),
    //addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )
