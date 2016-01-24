name := "mailgunner-service"

libraryDependencies ++= Seq(
  "org.scalatest"       %% "scalatest"  %  Versions.scalaTest % "test"
)

routesGenerator := InjectedRoutesGenerator