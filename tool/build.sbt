name := "mailgunner-tool"

libraryDependencies ++= Seq(
  "org.scalatest"       %% "scalatest"  %  Versions.scalaTest % "test"
)

assemblyJarName in assembly := "mailer.jar"

assemblyMergeStrategy in assembly := {
  case x if x.startsWith("org/apache/commons/logging/") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}