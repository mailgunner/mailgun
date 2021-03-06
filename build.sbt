name := "mailgunner"

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val tool = project
  .in(file("tool"))
  .settings(commonSettings: _*)
  .dependsOn(common)

// For a lack of a better name.
lazy val toolv2 = project
  .in(file("toolv2"))
  .settings(commonSettings: _*)
  .dependsOn(common)

lazy val service = project
  .in(file("service"))
  .enablePlugins(PlayScala)
  .settings(commonSettings: _*)
  .dependsOn(common)

lazy val common = project
  .in(file("common"))
  .settings(commonSettings: _*)
