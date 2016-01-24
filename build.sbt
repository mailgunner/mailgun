name := "mailgunner"

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val tool = project
  .in(file("tool"))
  .settings(commonSettings: _*)
