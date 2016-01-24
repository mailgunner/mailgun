name := "mailgunner-tool"

libraryDependencies ++= Seq(
  "com.typesafe.play"   %% "play-ws"    %  Versions.playWs,
  "com.typesafe.play"   %% "play-json"  %  Versions.playJson,
  "com.lihaoyi"         %% "scalatags"  %  Versions.scalaTags,
  "org.scalatest"       %% "scalatest"  %  Versions.scalaTest % "test"
)

assemblyJarName in assembly := "mailer.jar"

assemblyMergeStrategy in assembly := {
  case x if x.startsWith("org/apache/commons/logging/") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}