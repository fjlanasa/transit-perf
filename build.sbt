// The simplest possible sbt build file is just one line:

ThisBuild / scalaVersion := "2.13.3"
// That is, to create a valid sbt build, all you've got to do is define the
// version of Scala you'd like your project to use.

// ============================================================================

// Lines like the above defining `scalaVersion` are called "settings". Settings
// are key/value pairs. In the case of `scalaVersion`, the key is "scalaVersion"
// and the value is "2.13.3"

// It's possible to define many kinds of settings, such as:

ThisBuild / organization := "ch.epfl.scala"
ThisBuild / version := "1.0"

// Note, it's not required for you to define these three settings. These are
// mostly only necessary if you intend to publish your library's binaries on a
// place like Sonatype or Bintray.

// Want to use a published library in your project?
// You can define other libraries as dependencies in your build like this:
lazy val dependencies = new {
  val scalaParserCombinators =
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
  val jaxbApi = "javax.xml.bind" % "jaxb-api" % "2.3.1"
  val gtfsBindings = "com.google.transit" % "gtfs-realtime-bindings" % "0.0.4"
  val kafka = "org.apache.kafka" % "kafka-clients" % "2.7.0"
  val awsSDK = "com.amazonaws" % "aws-java-sdk" % "1.11.46"
  val commonsIO = "commons-io" % "commons-io" % "2.8.0"
}

lazy val commonDependencies = Seq(
  dependencies.scalaParserCombinators,
  dependencies.jaxbApi,
  dependencies.awsSDK,
  dependencies.gtfsBindings,
  dependencies.kafka,
  dependencies.commonsIO
)

lazy val root = (project in file("."))
  .aggregate(fetch, parse)
  .settings(
    libraryDependencies ++= commonDependencies
  )

lazy val fetch = (project in file("fetch"))
  .settings(name := "fetch", libraryDependencies ++= commonDependencies)

lazy val parse = (project in file("parse"))
  .settings(name := "parse", libraryDependencies ++= commonDependencies)
