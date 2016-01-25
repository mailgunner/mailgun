name := "mailgunner-tool"

libraryDependencies ++= Seq(
  "com.amazonaws"     %   "aws-java-sdk" % Versions.awsSdk
)

assemblyJarName in assembly := "mailerv2.jar"

assemblyMergeStrategy in assembly := {
  case x if x.startsWith("org/apache/commons/logging/") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}