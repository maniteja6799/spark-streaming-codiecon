
import sbt.Keys._

resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"


name := "CodieCon"

version := "1.0"

scalaVersion := "2.10.6"

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.6.1" % "provided" excludeAll (ExclusionRule(organization = "org.apache.logging"))
libraryDependencies += "org.apache.spark" % "spark-streaming_2.10" % "1.6.1" % "provided" excludeAll (ExclusionRule(organization = "org.apache.logging"))
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.0" excludeAll (ExclusionRule(organization = "org.apache.logging"))
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.7"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.7"
libraryDependencies += "com.typesafe" % "config" % "1.2.1"
libraryDependencies += "com.nethum.elasticsearch" % "shaded" % "1.0-SNAPSHOT"
libraryDependencies += "org.apache.logging.log4j" % "log4j-to-slf4j" % "2.8.2"


resolvers ++= Seq(
  "clojars" at "https://clojars.org/repo",
  "conjars" at "http://conjars.org/repo",
  "plugins" at "http://repo.spring.io/plugins-release",
  "sonatype" at "http://oss.sonatype.org/contet/groups/public/",
  "Online Play Repository" at "http://repo.typesafe.com/typesafe/simple/maven-releases/"
)


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
