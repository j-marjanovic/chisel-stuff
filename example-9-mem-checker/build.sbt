
ThisBuild / scalaVersion     := "2.12.12"
ThisBuild / version          := "0.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "hw-accel-mem-test",
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
  )
