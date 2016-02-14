import sbt._
import PlayKeys._

lazy val appName = "balloooon"

lazy val commonSettings = Seq(
  organization := "com.github.Jullil",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.7",
  resolvers ++= Seq(
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  ),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-cluster" % "2.4.0",
    "com.typesafe.akka" %% "akka-contrib" % "2.4.0",
    "com.typesafe.akka" %% "akka-slf4j" % "2.4.0",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % Test
  )
)

lazy val root = (project in file(".")).dependsOn(backend).aggregate(backend)
  .settings(commonSettings : _*)
  .settings(Seq(
    name := appName + "-web-client",
    libraryDependencies ++= Seq(
      jdbc,
      cache,
      ws,
      specs2 % Test,
      "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
      "org.webjars" % "bootstrap" % "3.0.0",
      "org.webjars" % "sockjs-client" % "1.0.2"
    ),
    playMonitoredFiles ++= (sourceDirectories in(Compile, TwirlKeys.compileTemplates)).value,
    routesGenerator := InjectedRoutesGenerator
//    discoveredMainClasses in Compile ++= (discoveredMainClasses in backend in Compile).value,
    //    run := {
    //      (run in backend in Compile).evaluated
    //    }//,
    //mainClass in Compile ++= (mainClass in backend in Compile).value
//    fullClasspath in Compile ++= (fullClasspath in backend in Compile).value,
//    fullClasspath in Runtime ++= (fullClasspath in backend in Runtime).value
  ))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLayoutPlugin)

lazy val backend = (project in file("backend"))
  .settings(commonSettings: _*)
  .settings(Seq(
    name := appName + "-backend"
  ))
